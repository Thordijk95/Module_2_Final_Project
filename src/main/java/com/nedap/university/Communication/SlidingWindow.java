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
import java.util.Timer;

public class SlidingWindow extends AbstractWindow {

  final int maxSeqNum = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2), (DatagramProperties.SEQUENCE_NUMBER_SIZE*8));
  final int SWS = maxSeqNum/2;

  int LAF;
  int LFS;

  ArrayList<InterfacePacket> slidingWindowPackets;

  Util util;

  public SlidingWindow(String directory) {
    super(directory);
    slidingWindowPackets = new ArrayList<>();
    util = new Util();
  }

  @Override
  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      boolean first, boolean ack, int packetCounter, String filename, byte[] dataPacket) throws IOException {
    InterfacePacket outboundPacket =
        new OutboundPacket(address, port, requestType, first, false, packetCounter, filename, dataPacket);
    sendPacket(socket, address, port, outboundPacket);
  }

  @Override
  public void sendPacket(DatagramSocket socket, InetAddress address, int port, InterfacePacket packet) throws IOException {
    DatagramPacket outboundDatagram
        = new DatagramPacket(packet.getData(), packet.getData().length, address, port);
    socket.send(outboundDatagram);
    timeout.createTimer(packet, new Timer(), socket);
    slidingWindowPackets.add(packet);
  };

  @Override
  public InterfacePacket receive(DatagramSocket socket) throws IOException {
    DatagramPacket datagramPacket = new DatagramPacket(new byte[DatagramProperties.DATAGRAMSIZE], DatagramProperties.DATAGRAMSIZE);
    socket.receive(datagramPacket);
    return new InboundPacket(datagramPacket);
  }

  public void verifyAcknowledgement(InterfacePacket ackPacket) {
    // Check if the sequence number matches one of the send packets
    for (InterfacePacket packet : slidingWindowPackets) {
      if (packet.getSequenceNumber() == ackPacket.getSequenceNumber() && ackPacket.isAcknowledgement()
          && packet.getRequestType() == ackPacket.getRequestType()) {
        addAcknowledgedPacket(packet);
        LAF = packet.getSequenceNumber();
      }
    }
  }

  public boolean inSendWindow(int sequenceNumber) {
    // Check if the sequence number could already have wrapped
    if (LAF + SWS > maxSeqNum) {
      // sequence numbers can wrap back to zero
      return sequenceNumber < LAF + SWS - maxSeqNum || (sequenceNumber > LAF
          && sequenceNumber < SWS);
    } else {
      // sequence numbers should be between LAF and SWS
      return (sequenceNumber >= LAF && sequenceNumber < (LAF + SWS));
    }
  }


  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType, ArrayList<byte[]> dataList, String filename) throws IOException {
    int packetCount = dataList.size();
    int sequenceNumber = 0;
    boolean first = true;
    for (byte[] dataPacket : dataList) {
      if (inSendWindow(sequenceNumber)) {
        dataPacket = util.lastPacketInList(dataPacket, dataList);
        send(socket, address, port, requestType, first, false, sequenceNumber, filename, dataPacket);
        LFS = sequenceNumber;
        sequenceNumber++;
        first = false;
        verifyAcknowledgement(receive(socket));
      }
      if (sequenceNumber == Math.pow(2,8)) { // Sequence number field is 8 bit, after number 2^8 wrap back to 0
        sequenceNumber = 0;
      }
    }
    sendPacket(socket, address, port, new ClosingPacket(requestType));
  }

  @Override
  public byte[] receiver(DatagramSocket socket, InetAddress address, int port,
      Requests requestsType) throws IOException {
    // Should not be used
    return null;
  }

}
