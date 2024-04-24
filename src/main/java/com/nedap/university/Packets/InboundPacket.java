package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import java.net.DatagramPacket;

public class InboundPacket extends AbstractPacket {

  // Create a packet to receive inbound data
  public InboundPacket(DatagramPacket inboundDatagram) {
    byte[] data = new byte[inboundDatagram.getLength()];
    System.arraycopy(inboundDatagram.getData(), 0, data, 0, inboundDatagram.getLength());
    setPort(inboundDatagram.getPort());
    setAddress(inboundDatagram.getAddress());
    setHeader(data);
    setData(data, HEADER_SIZE, 0, inboundDatagram.getLength()- (HEADER_SIZE));
    parseHeader();
  }

}
