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

  final int SWS = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2),
      (DatagramProperties.SEQUENCE_NUMBER_SIZE * 8)) / 4;

  int LAR = 0;
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
    return new InboundPacket(datagramPacket);
  }

  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      ArrayList<byte[]> dataList, String filename) throws IOException {
    int sequenceNumber = 0;
    int wrapCounter = 0;
    int packetCounter = 0;
    LAR = -1;
    LFS = -1;
    boolean first = true;
    boolean last = false;
    boolean stop = false;

    // send an intial burst of packets to fill the send window
    for(int i = 0; i <= Math.min(dataList.size()-1, SWS); i++) {
      sequenceNumber = i;
      packetCounter = sequenceNumber;
      byte[] dataPacket = dataList.get(packetCounter);
      if (i == dataList.size() - 1) {
        // SWS is larger than total number of packets, everything was send in the burst
        last = true;
      }
      send(socket, address, port, requestType, first, last, false, sequenceNumber, filename,
          dataPacket);
      LFS = sequenceNumber;
      first = false;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        System.out.println("Sleep was interrupted");
      }
    }
    sequenceNumber++;
    packetCounter++;

    while(!stop){
      while(last) {
        System.out.println("last packet has been sent, wait for all the acks or timeouts that trigger acks");
        stop = true;
        if (slidingWindowPackets.isEmpty()) {
          System.out.println("last packet acknowledged");
          return;
        }
        if (slidingWindowPackets.size() == 1) {
          System.out.println("Last packet needs ack");
        }
        while (!(verifyAcknowledgement(receive(socket)))) {
          System.out.println("Waiting for acknowledgement");
          // do nothing
        }
      }
      byte[] dataPacket = dataList.get(packetCounter);
      if (sequenceNumber == 254) {
        System.out.println("wait!");
      }
      if (inWindow(LAR, SWS, maxSeqNum, sequenceNumber)) {
        // send the next packet
        if (Arrays.equals(dataList.get(dataList.size() - 1), dataPacket)) {
          last = true;
        }
        send(socket, address, port, requestType, first, last, false, sequenceNumber, filename,
            dataPacket);
        LFS = sequenceNumber;

        sequenceNumber++;
        packetCounter = sequenceNumber + maxSeqNum*wrapCounter;
      } else {
        System.out.println("Packet not in sender window " + LAR + ":" + LFS);
        System.out.println("Waiting for acknowledgement to continue");
        while (!(verifyAcknowledgement(receive(socket)))) {
          // do nothing
        }
      }
      if (sequenceNumber == maxSeqNum) { // Sequence number field is 8 bit, after number 2^8 wrap back to 0
        System.out.println("Wrap arround");
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
        LAR = packet.getSequenceNumber();
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

  @Override
  public boolean inWindow(int lowerBound, int windowSize, int maxSeqNum, int seqNum) {
    // Check if the sequence number could already have wrapped
    if (lowerBound + windowSize >= maxSeqNum) {
      // sequence numbers can wrap back to zero
      return seqNum < lowerBound + windowSize - maxSeqNum || (seqNum > lowerBound
          && seqNum < maxSeqNum);
    } else {
      // sequence numbers should be between LAF and SWS
      return (seqNum >= lowerBound && seqNum < (lowerBound + windowSize));
    }
  }
}
