package com.nedap.university.util;

import static java.lang.Math.max;

import com.nedap.university.Requests;

// Deprecated!!!

public class PacketConstructor {

  ChecksumCalculator checksumCalculator;
  Conversions conversions;

  public PacketConstructor() {
    checksumCalculator = new ChecksumCalculator();
    conversions = new Conversions();
  }

  public byte[] constructPacket(int headerSize, int sourceIp, int destinationIp, int packetLength,
      int requestType, boolean acknowledgement, int sequenceNumber, byte[] data) {
    byte[] header = constructHeader(headerSize, sourceIp, destinationIp, packetLength, requestType, acknowledgement, sequenceNumber);
    byte[] packet = new byte[header.length + data.length]; {};
    System.arraycopy(header, 0, packet, 0, header.length);
    System.arraycopy(data, 0, packet, header.length, data.length);
    return packet;

  }

  private byte[] constructHeader(int headerSize, int sourceIp, int destinationIp, int packetLength, int requestType, boolean acknowledgement, int sequenceNumber) {
    int infoField = conversions.infoField(requestType, acknowledgement, sequenceNumber);
    int checksum = checksumCalculator.checksumCalculator(sourceIp, destinationIp, packetLength, infoField);

    byte[] header = new byte[headerSize]; // 8 8 bit bytes or 4 16 bit words split to bytes
    header[0] = (byte) ((sourceIp >>> 8) & 0xFF);
    header[1] = (byte) (sourceIp & 0xFF);
    header[2] = (byte) ((destinationIp >>> 8) & 0xFF);
    header[3] = (byte) (destinationIp & 0xFF);
    header[4] = (byte) ((packetLength >>> 8) & 0xFF);
    header[5] = (byte) (packetLength & 0xFF);
    header[6] = (byte) ((infoField >>>8) & 0xFF);
    header[7] = (byte) (infoField & 0xFF);
    header[8] = (byte) ((checksum >>> 8) & 0xFF);
    header[9] = (byte) (checksum & 0xFF);
    return header;
  }
}
