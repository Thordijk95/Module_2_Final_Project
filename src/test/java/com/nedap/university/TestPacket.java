package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.util.Packet;
import org.junit.jupiter.api.Test;

public class TestPacket {

  @Test
  public void testParseHeader() {
    Packet packet = new Packet(Requests.UPLOAD, false, 0, "Tiny.pdf", new byte[0]);
    assertEquals(Requests.UPLOAD, packet.getRequestType());
    packet.parseHeader();
    assertEquals(Requests.UPLOAD, packet.getRequestType());
  }

}
