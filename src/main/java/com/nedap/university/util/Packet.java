package com.nedap.university.util;

import static com.nedap.university.util.DatagramProperties.HEADERSIZE;

import com.nedap.university.Requests;
import com.nedap.university.util.*;

public class Packet {

  ChecksumCalculator checksumCalculator = new ChecksumCalculator();

  private byte[] header;
  private byte[] data;
  public Requests requestType;
  public boolean acknowledgement;
  public int sequenceNumber;

  // Create an empty packet
  public Packet() {
    header = new byte[HEADERSIZE];
    requestType = Requests.EMPTY;
    acknowledgement = false;
    sequenceNumber = 0;
    setData(new byte[1]);
  }

  // Create a packet with header and data
  public Packet(Requests requestType, boolean acknowledgement, int sequenceNumber, byte[] data) {
    header = new byte[HEADERSIZE];
    this.data = new byte[Math.min(DatagramProperties.DATAGRAMSIZE, HEADERSIZE + data.length)];
    this.requestType = requestType;
    this.acknowledgement = acknowledgement;
    this.sequenceNumber = sequenceNumber;
    setData(data);
  }

  private boolean validPacket() {
    return false;
  }

  public void setData(byte[] data) {
    setHeader();
    System.arraycopy(header, 0, this.data, 0, HEADERSIZE);
    System.arraycopy(data, 0, this.data, HEADERSIZE, data.length);
  }

  public byte[] getData() {
    return data;
  }

  public void setHeader() {
    header[0] = (byte) (((acknowledgement ? 0x01 : 0x00) << 4) | (requestType.getValue() & 0xFF)); ;
    header[1] = (byte) (sequenceNumber & 0xFF);
  }

  public int getHeader() {
    return ((header[0] << 8) & 0xFF00) | (header[1] & 0xFF);
  }

  public Requests getRequestType() {
    return requestType;
  }

  public boolean isAcknowledgement() {
    return acknowledgement;
  }

  public int getSequenceNumber() {
    return sequenceNumber;
  }



  public void setChecksum() {}

}
