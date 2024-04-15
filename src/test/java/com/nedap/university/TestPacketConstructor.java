package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.util.PacketConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPacketConstructor {

  PacketConstructor packetConstructor;

  @BeforeEach
  public void setup() {
    packetConstructor = new PacketConstructor();
  }

  @Test
  public void testPacketConstructor() {
    int sourcePort = 0b00010101_00001001;
    int destinationPort = 0b00010101_00001001;
    int fieldLength = 0b00000000_00000100;
    int expectedChecksum = 0b11010101_11101001;
    int request = Requests.EMPTY.getValue();
    boolean acknowledgment = false;
    int sequenceNumber = 0;
    byte[] requestBytes = new byte[] {(byte)((request >>> 8) & 0xFF), (byte)(request & 0xFF)};

    byte[] expectedPacket = new byte[] {0b00010101, 0b00001001, 0b00010101, 0b00001001,
        0b00000000, 0b00000100, requestBytes[0], requestBytes[1], (byte) 0b11010101, (byte) 0b11101001, 0};

    byte[] packet = packetConstructor.constructPacket(10, sourcePort, destinationPort, fieldLength, request, acknowledgment, sequenceNumber, new byte[] {0});

    assertArrayEquals(expectedPacket, packet);
  }

}
