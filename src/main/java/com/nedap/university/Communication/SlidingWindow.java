package com.nedap.university.Communication;

import com.nedap.university.Packets.AbstractPacket;
import com.nedap.university.Packets.AckPacket;
import com.nedap.university.Packets.ClosingPacket;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import com.nedap.university.util.Timeout;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class SlidingWindow extends AbstractWindow {

  final int maxSeqNum = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2),
      (DatagramProperties.SEQUENCE_NUMBER_SIZE * 8)) - 1;
  final int SWS = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2),
      (DatagramProperties.SEQUENCE_NUMBER_SIZE * 8)) / 2;

  int LAF = 0;
  int LFS;

  ArrayList<InterfacePacket> slidingWindowPackets;
  Timeout timeout;
  Util util;

  public SlidingWindow(String directory) {
    super(directory);
    slidingWindowPackets = new ArrayList<>();
    timeout = new Timeout(this);
    util = new Util();
  }

  @Override
  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      boolean first, boolean last, boolean ack, int packetCounter, String filename,
      byte[] dataPacket) throws IOException {
    InterfacePacket outboundPacket =
        new OutboundPacket(address, port, requestType, first, last, false, packetCounter, filename,
            dataPacket);
    sendPacket(socket, address, port, outboundPacket);
  }

  @Override
  public void sendPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    DatagramPacket outboundDatagram
        = new DatagramPacket(packet.getData(), packet.getData().length, address, port);
    socket.send(outboundDatagram);
    System.out.println("Sending packet with seq num: " + packet.getSequenceNumber());
    System.out.println("Sending " + packet.getData().length + " bytes of data");
    timeout.createTimer(packet, new Timer(), socket);
    slidingWindowPackets.add(packet);
  }

  @Override
  public InterfacePacket receive(DatagramSocket socket) throws IOException {
    DatagramPacket datagramPacket = new DatagramPacket(new byte[DatagramProperties.DATA_SIZE],
        DatagramProperties.DATA_SIZE);
    socket.receive(datagramPacket);
    System.out.println("Received packet with address: " + datagramPacket.getAddress());
    return new InboundPacket(datagramPacket);
  }

  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      ArrayList<byte[]> dataList, String filename) throws IOException {
    int sequenceNumber = 0;
    int wrapCounter = 0;
    int packetCounter = 0;
    LAF = 0;
    LFS = -1;
    boolean first = true;
    boolean last = false;
    while(!last){
      byte[] dataPacket = dataList.get(packetCounter);
      if (inWindow(LAF, SWS, maxSeqNum, sequenceNumber)) {
        if (Arrays.equals(dataList.get(dataList.size() - 1), dataPacket)) {
          last = true;
        }
        send(socket, address, port, requestType, first, last, false, sequenceNumber, filename,
            dataPacket);
        LFS = sequenceNumber;

        first = false;
        while (!(verifyAcknowledgement(receive(socket)))) {
          System.out.println("Waiting for acknowledgement");
          // do nothing
        }
        sequenceNumber++;
        packetCounter++;
      } else {
        System.out.println("Packet not in sender window " + LAF + ":" + LFS);
      }
      if (sequenceNumber == 127) {
        System.out.println("break");
      }
      if (sequenceNumber == Math.pow(2,
          8)) { // Sequence number field is 8 bit, after number 2^8 wrap back to 0
        sequenceNumber = 0;
        wrapCounter++;
      }
    }
  }

  @Override
  public byte[] receiver(DatagramSocket socket, InetAddress address, int port,
      Requests requestsType) throws IOException {
    // Should not be used
    return null;
  }

  @Override
  public boolean verifyAcknowledgement(InterfacePacket ackPacket) {
    // Check if the sequence number matches one of the send packets
    InterfacePacket ackedPacket = null;
    for (InterfacePacket packet : slidingWindowPackets) {
      if (packet.getSequenceNumber() == ackPacket.getSequenceNumber()
          && ackPacket.isAcknowledgement()
          && packet.getRequestType() == ackPacket.getRequestType()) {
        addAcknowledgedPacket(packet);
        ackedPacket = packet;
        LAF = packet.getSequenceNumber();
        System.out.println("Received acknowledgement for: " + packet.getRequestType() + " "
            + packet.getSequenceNumber());
        break;
      }
    }
    if (ackedPacket != null) {
      slidingWindowPackets.remove(ackedPacket);
      return true;
    }
    return false;
  }

  @Override
  public boolean verifyNewPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    System.out.println("VerifyNewPacket method should not be used from the sender class");
    return false;
  }
}
