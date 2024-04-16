package com.nedap.university.util;

import java.util.Arrays;

// Deprecated

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
    System.out.println("Evaluating checksum!");
    ChecksumCalculator checksumCalculator = new ChecksumCalculator();

    byte[] header = splitHeader(packet, headerSize);
    int sourcePort = getSourcePort(header);
    System.out.println("SourcePort:" + sourcePort);
    int destinationPort = getDestinationPort(header);
    System.out.println("DestinationPort: " + destinationPort);
    int fieldLength = getFieldLength(header);
    System.out.println("FieldLength " + fieldLength);
    int requestType = getRequestType(header);
    System.out.println("RequestType: " + requestType);
    boolean acknowledgement = getAcknowledgement(header);
    System.out.println("Acknowledgement: " + acknowledgement);
    int sequenceNumber = getSequenceNumber(header);
    System.out.println("SequenceNumber: " + sequenceNumber);

    int infoField = conversions.infoField(requestType, acknowledgement, sequenceNumber);
    System.out.println("InfoField: " + infoField);
    int checksum = getChecksum(header);
    System.out.println("Checksum: " + checksum);
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
    int msb = header[4] << 8;
    System.out.println(msb);
    int lsb = header[5];
    System.out.println(lsb);
    int full_byte = msb | lsb;
    System.out.println(full_byte);
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
