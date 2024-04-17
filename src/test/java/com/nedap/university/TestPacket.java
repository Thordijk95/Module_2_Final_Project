package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.util.Packet;
import org.junit.jupiter.api.Test;

public class TestPacket {

  Requests requestType = Requests.UPLOAD;
  boolean first = false;
  boolean acknowledged = false;
  int sequenceNumber = 0;
  String filename = "Tiny.pdf";


  @Test
  public void testRequestType() {
    Packet packet = new Packet(requestType, first, acknowledged, sequenceNumber, filename, new byte[0]);
    assertEquals(Requests.UPLOAD, packet.getRequestType());
    packet.parseHeader();
    assertEquals(Requests.UPLOAD, packet.getRequestType());
  }

  @Test
  public void testFilename() {
    Packet packet = new Packet(requestType, first, acknowledged, sequenceNumber , filename, new byte[0]);
    assertEquals(filename, packet.fileName + "." + packet.fileType);
    packet.parseHeader();
    assertEquals(filename, packet.fileName + "." + packet.fileType);
  }
}
