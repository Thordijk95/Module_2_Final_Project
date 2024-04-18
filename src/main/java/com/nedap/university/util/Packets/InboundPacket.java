package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import java.net.DatagramPacket;

public class InboundPacket extends AbstractPacket {

  // Create a packet to receive inbound data
  public InboundPacket(DatagramPacket inboundDatagram) {
    byte[] data = new byte[inboundDatagram.getLength()];
    System.arraycopy(inboundDatagram.getData(), 0, data, 0, inboundDatagram.getLength()-1);
    System.out.println("copied data : " + data.length);
    setHeader(data);
    System.out.println("header set");
    setData(data, HEADER_SIZE-1, 0, inboundDatagram.getLength()- (HEADER_SIZE-1));
    parseHeader();
  }

}
