package com.nedap.university.util;

import java.util.Arrays;

public class PacketParser {

  Conversions conversions;
  public PacketParser() {
    conversions = new Conversions();
  }

  private byte[] splitHeader(byte[] packet, int headerSize) {
    byte[] header = new byte[headerSize];
    System.arraycopy(packet, 0, header, 0, headerSize);
    return header;
  }

  private byte[] splitData(byte[] packet, int headerSize) {
    byte[] data = new byte[packet.length - headerSize];
    System.arraycopy(packet, headerSize, data, 0, data.length);
    return data;
  }

  public boolean evaluateChecksum(byte[] packet, int headerSize) {
    ChecksumCalculator checksumCalculator = new ChecksumCalculator();

    byte[] header = splitHeader(packet, headerSize);
    int sourcePort = getSourcePort(header);
    int destinationPort = getDestinationPort(header);
    int fieldLength = getFieldLength(header);
    int requestType = getRequestType(header);
    boolean acknowledgement = getAcknowledgement(header);
    int sequenceNumber = getSequenceNumber(header);

    int infoField = conversions.infoField(requestType, acknowledgement, sequenceNumber);

    int checksum = getChecksum(header);

    int calculatedChecksum = checksumCalculator.checksumCalculator(sourcePort, destinationPort, fieldLength, infoField);
    return checksum == calculatedChecksum;
  }

  private int getSourcePort(byte[] header) {
    return (header[0] << 8 | header[1]) & 0xFFFF;
  }

  private int getDestinationPort(byte[] header) {
    return (header[2] << 8 | header[3]) & 0xFFFF;
  }

  private int getFieldLength(byte[] header) {
    return (header[4] << 8 | header[5]) & 0xFFFF;
  }

  private int getRequestType(byte[] header) {
    return (header[6] >>> 1) & 0xFF;
  }

  private boolean getAcknowledgement(byte[] header) {
    return (header[6] & 0x01) == 0x01;
  }

  private int getSequenceNumber(byte[] header) {
    return (header[7] & 0xFF);
  }


  private int getChecksum(byte[] header) {
    return (((header[8] << 8) & 0xFF00) | (header[9] & 0xFF)) & 0xFFFF;
  }

}
