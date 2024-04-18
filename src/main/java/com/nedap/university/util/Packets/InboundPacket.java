package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADERSIZE;

public class InboundPacket extends AbstractPacket {

  // Create a packet to receive inbound data
  public InboundPacket(byte[] data) {
    setHeader(data);
    setData(data, HEADERSIZE, 0, data.length-HEADERSIZE);
    parseHeader();
  }

}
