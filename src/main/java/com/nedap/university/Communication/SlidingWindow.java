package com.nedap.university.Communication;

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

public class SlidingWindow {

  final int maxSeqNum = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2), (DatagramProperties.SEQUENCE_NUMBER_SIZE*8));
  final int SWS = maxSeqNum/2;

  int LAF;
  int LFS;

  ArrayList<InterfacePacket> slidingWindowPackets;
  ArrayList<InterfacePacket> acknowledgedPackets;

  Util util;

  public SlidingWindow() {
    acknowledgedPackets = new ArrayList<>();
    slidingWindowPackets = new ArrayList<>();
    util = new Util();
  }

  public void addAcknowledgedPacket(InterfacePacket packet) {
    acknowledgedPackets.add(packet);
  }

  public void removeAcknowledgedPacket(InterfacePacket packet) {
    acknowledgedPackets.remove(packet);
  }

  public ArrayList<InterfacePacket> getAcknowledgedPackets() {
    return acknowledgedPackets;
  }

  public void verifyAcknowledgement(InterfacePacket ackPacket) {
    // Check if the sequence number matches one of the send packets
    for (InterfacePacket packet : slidingWindowPackets) {
      if (packet.getSequenceNumber() == ackPacket.getSequenceNumber() && ackPacket.isAcknowledgement()
          && packet.getRequestType() == ackPacket.getRequestType()) {
        System.out.println("Succesfully acknowledged packet: " + ackPacket.getSequenceNumber());
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

  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType, Timeout timeout, boolean first, boolean ack, int packetCounter,
      String filename, byte[] dataPacket) throws IOException {
    InterfacePacket outboundPacket =
        new OutboundPacket(address, port, requestType, first, false, packetCounter, filename, dataPacket);
    DatagramPacket outboundDatagramPacket
        = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length-1, address, port);
    socket.send(outboundDatagramPacket);
    timeout.createTimer(outboundPacket, new Timer(), socket);
    LFS = outboundPacket.getSequenceNumber();
    slidingWindowPackets.add(outboundPacket);
  }

  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType, Timeout timeout, ArrayList<byte[]> dataList, String filename) throws IOException {
    int packetCount = dataList.size();
    int packetCounter = 0;
    boolean first = true;
    for (byte[] dataPacket : dataList) {
      if (inSendWindow(packetCounter)) {
        dataPacket = util.lastPacketInList(dataPacket, dataList);
        System.out.println("Sending packet " + (packetCounter+1) + ": " + packetCount);
        send(socket, address, port, requestType, timeout, first, false, packetCounter, filename, dataPacket);
        packetCounter++;
        first = false;

        byte[] buffer = new byte[DatagramProperties.DATAGRAMSIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);
        InterfacePacket inboundPacket = new InboundPacket(response);
        verifyAcknowledgement(inboundPacket);
      }


      if (packetCounter == Math.pow(2,8)) { // Sequence number field is 8 bit, after number 2^8 wrap back to 0
        packetCounter = 0;
      }
    }
  }

}
