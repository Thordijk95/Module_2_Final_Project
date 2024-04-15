package com.nedap.university;


import static org.junit.jupiter.api.Assertions.*;

import com.nedap.university.util.ChecksumCalculator;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.PacketConstructor;
import com.nedap.university.util.PacketParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestChecksum {
  ChecksumCalculator checksumCalculator;
  Conversions conversions;

  int headerSize = 10;
  @BeforeEach
  public void setup() {
    checksumCalculator = new ChecksumCalculator();
    conversions = new Conversions();
  }
  @Test
  public void testOnesComplement (){
    int value = 0b00000000_01010101;
    int expectedComplement = 0b11111111_10101010;

    byte[] complement = checksumCalculator.onesComplement(value);

    assertEquals(expectedComplement, conversions.fromByteArrayToInt(checksumCalculator.onesComplement(value)));
  }

  @Test
  public void testChecksumCalculator() {
    int sourcePort = 0b00010101_00001001;
    int destinationPort = 0b00010101_00001001;
    int fieldLength = 0b00000000_00000100;
    int infoField = 0b00000000_00000000;

    int expectedChecksum = 0b11010101_11101001;
    int checkSum = checksumCalculator.checksumCalculator(sourcePort, destinationPort, fieldLength, infoField);

    assertEquals(expectedChecksum, checkSum);
  }

  @Test
  public void testPacketConstructorParser() {
    PacketConstructor packetConstructor = new PacketConstructor();
    PacketParser packetParser = new PacketParser();

    int sourcePort = 0b00010101_00001001;
    int destinationPort = 0b00010101_00001001;
    int fieldLength = 0b00000000_00000100;
    int expectedChecksum = 0b11010101_11101001;

    byte[] packet = packetConstructor.constructPacket(10, sourcePort,destinationPort, fieldLength,
        Requests.EMPTY.getValue(), false, 0, new byte[] {0});
    assert(packetParser.evaluateChecksum(packet, 10));

  }
}
