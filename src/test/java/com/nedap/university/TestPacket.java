package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.Communication.Requests;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

public class TestPacket {

  InetAddress address = InetAddress.getByName("172.16.1.1");
  int port = 8080;



  public TestPacket() throws UnknownHostException {
  }

  @Test
  public void testPacketCreation() {
    InterfacePacket outboundPacket = new OutboundPacket(address, port, Requests.UPLOAD, false, false,
        false, 5, "Tiny.pdf", new byte[] {0x00});

    DatagramPacket datagram = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, address, port);

    InterfacePacket inboundPacket = new InboundPacket(datagram);

    assertEquals(outboundPacket.getFileName(), inboundPacket.getFileName());
    assertEquals(outboundPacket.getSequenceNumber(), inboundPacket.getSequenceNumber());
    assertEquals(outboundPacket.getPort(), inboundPacket.getPort());
    assertEquals(outboundPacket.getAddress(), inboundPacket.getAddress());
  }

}
