package com.nedap.university.Communication;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Packets.AckPacket;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.util.Timeout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ReceiveWindow extends AbstractWindow {

  Timeout timeout;

  HashMap<Integer, InterfacePacket> receiveWindow;
  ArrayList<byte[]> dataList;


  final int RWS = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2),
      (DatagramProperties.SEQUENCE_NUMBER_SIZE * 8)) / 4;
  int LFR = -1;
  int LAF = LFR + RWS;

  int SEQNUMTOACK;

  public ReceiveWindow(String directory) {
    super(directory);
    receiveWindow = new HashMap<>();
    timeout = new Timeout(this);
  }

  @Override
  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      boolean first, boolean last, boolean ack, int packetCounter, String filename,
      byte[] dataPacket) throws IOException {
    // Should not be used from receivewindow
  }

  @Override
  public void sendPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    // should not be used from receive window
  }

  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      ArrayList<byte[]> dataList, String filename) throws IOException {
    // Should not be used from receive window
  }

  @Override
  public InterfacePacket receive(DatagramSocket socket) throws IOException {
    DatagramPacket inboundDatagram = new DatagramPacket(new byte[DatagramProperties.DATAGRAMSIZE],
        DatagramProperties.DATAGRAMSIZE);
    socket.receive(inboundDatagram);
    return new InboundPacket(inboundDatagram);
  }

  @Override
  public byte[] receiver(DatagramSocket socket, InetAddress address, int port,
      Requests requestsType) throws IOException {
    dataList = new ArrayList<>();
    SEQNUMTOACK = 0;
    LFR = -1;
    LAF = LFR + RWS;
    // start the receiver
    System.out.println("Starting receiver!!!");
    while (true) {
      // receive the next packet
      InterfacePacket downloadPacket = receive(socket);
      if (verifyNewPacket(socket, address, port, downloadPacket)) {
        System.out.println("Received " + downloadPacket.getData().length +" bytes of data");
        byte[] data = downloadPacket.getData();
        dataList.add(data);
        if (downloadPacket.isLastPacket()) {
          return Conversions.fromDataListToByteArray(dataList);
        }
      }
    }
  }

  @Override
  public boolean verifyAcknowledgement(InterfacePacket ackPacket) {
    System.out.println("This method should not be used from the receiver class!!");
    return false;
  }

  @Override
  public boolean verifyNewPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    // Check if packet is valid and not already acknowledged (e.g. acknowledgement was lost, this is resend)
    boolean returnValue = false;

    if (packet.isValidPacket() && !getAcknowledgedPackets().contains(packet)) {
      if (inWindow(LFR, RWS, maxSeqNum, packet.getSequenceNumber())) {
        System.out.println("Packet in window");
        if (packet.getSequenceNumber() == SEQNUMTOACK) {
          acknowledgePacket(socket, address, port, packet);
          SEQNUMTOACK++;
          updateWindow();
          // Check if any other packets can be acknowledged
          processReceiveWindow(socket, address, port);
          returnValue = true;
        } else if (!receiveWindow.containsKey(packet.getSequenceNumber())){
          System.out.println("Sequence number: " + packet.getSequenceNumber());
          System.out.println("SeqNumToAck: " + SEQNUMTOACK);
          acknowledgePacket(socket, address, port, packet);
          // Packet not the next to acknowledge, store for later
          receiveWindow.put(packet.getSequenceNumber(), packet);
        }  else {

        }
      } else {
        System.out.println("Packet not in window!!!");
        System.out.println("Sequence number: " + packet.getSequenceNumber());
        System.out.println("Current window: " + LFR +":"+ LAF);
      }
    } else if (packet.isValidPacket() && getAcknowledgedPackets().contains(packet)) {
      // Packet has already been processed and acknowledged but acknowledgement was apparently lost
      // resend the acknowledgement but do not use the data
      acknowledgePacket(socket, address, port, packet);
      if (!receiveWindow.containsKey(packet.getSequenceNumber())) {
        // Packet not the next to acknowledge, store for later
        receiveWindow.put(packet.getSequenceNumber(), packet);
      }
    } else {
      System.out.println("Dropped the packet 1");
    }
    if (SEQNUMTOACK == maxSeqNum) {
      SEQNUMTOACK = 0;
    }
    return returnValue;
  }

  @Override
  public boolean inWindow(int lowerBound, int windowSize, int maxSeqNum, int seqNum) {
    System.out.println("Check in window");
    // Check if the sequence number could already have wrapped
    if (lowerBound + windowSize >= maxSeqNum) {
      // sequence numbers can wrap back to zero
      return seqNum <= lowerBound + windowSize - maxSeqNum || (seqNum >= lowerBound
          && seqNum <= maxSeqNum);
    } else {
      // sequence numbers should be between LAF and SWS
      return (seqNum >= lowerBound && seqNum <= (lowerBound + windowSize));
    }
  }

  public void updateWindow() {
    LFR = SEQNUMTOACK;
    LAF = LFR + RWS;
    if (LAF >= maxSeqNum) {
      LAF = LAF - maxSeqNum;
    }
  }

  public void processReceiveWindow(DatagramSocket socket, InetAddress address, int port) throws IOException{
    for(int i = LFR; i < maxSeqNum; i++) {
      if (!receiveWindow.containsKey(i)) {
        // found a gap, stop and wait for this packet
        break;
      }
      dataList.add(receiveWindow.get(i).getData());
      acknowledgePacket(socket, address, port, receiveWindow.get(i));
      SEQNUMTOACK++;
      receiveWindow.remove(i);
    }
    updateWindow();
  }
}