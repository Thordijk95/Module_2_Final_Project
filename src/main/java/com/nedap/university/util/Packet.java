package com.nedap.university.util;

import static com.nedap.university.util.DatagramProperties.DATAGRAMSIZE;
import static com.nedap.university.util.DatagramProperties.HEADERSIZE;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.InvalidRequestValue;
import com.nedap.university.util.*;

public class Packet {

  ChecksumCalculator checksumCalculator = new ChecksumCalculator();

  private byte[] header;
  private byte[] data;
  public Requests requestType;
  public boolean acknowledgement;
  public int sequenceNumber;
  public String fileName;
  public String fileType;

  // Create an empty packet
  public Packet() {
    header = new byte[HEADERSIZE];
    requestType = Requests.EMPTY;
    acknowledgement = false;
    sequenceNumber = 0;
    fileName = "";
    fileType = "";
    setData(new byte[1]);
  }

  // Create a packet to receive inbound data
  public Packet(byte[] data) {
    header = new byte[HEADERSIZE];
    this.data = new byte[data.length-HEADERSIZE];
    System.arraycopy(data, 0, header, 0, HEADERSIZE);
    System.arraycopy(data, HEADERSIZE, this.data, 0, data.length-HEADERSIZE);
    parseHeader();
  }

  // Create an acknowledgement packet with header and no data to send
  public Packet(Requests requestType, boolean acknowledgement, int sequenceNumber) {
    header = new byte[HEADERSIZE];
    this.requestType = requestType;
    this.acknowledgement = acknowledgement;
    this.sequenceNumber = sequenceNumber;
    this.fileName = "";
    this.fileType = "";
    setHeader();
    data = header;
  }

  // Create a packet with header and data to send
  public Packet(Requests requestType, boolean acknowledgement, int sequenceNumber, String fileName, byte[] data) {
    header = new byte[HEADERSIZE];
    this.data = new byte[Math.min(DatagramProperties.DATAGRAMSIZE, HEADERSIZE + data.length)];
    this.requestType = requestType;
    this.acknowledgement = acknowledgement;
    this.sequenceNumber = sequenceNumber;
    String[] file = fileName.split("\\.");
    this.fileName = file[0];
    this.fileType = file[1];
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
    // byte 0 - 19 = filename
    byte[] fileNameBytes = fileName.getBytes();
    for (int i = 0; i < fileNameBytes.length; i++) {
      header[i] = fileNameBytes[i];
    }
    // byte 20-23 = file type
    byte[] fileTypeBytes = fileType.getBytes();
    for (int i = 0; i < fileTypeBytes.length; i++) {
      header[DatagramProperties.FILETYPEOFFSET+i] = fileTypeBytes[i];
    }
    // byte
    header[DatagramProperties.ACKNOWLEDGMENT_REQUESTOFFSET] =
        (byte) (((acknowledgement ? 0x01 : 0x00) << 4) | (requestType.getValue() & 0xFF)); ;
    header[DatagramProperties.SEQUENCE_NUMBEROFFSET] =
        (byte) (sequenceNumber & 0xFF);
  }

  public int getHeader() {
    return ((header[0] << 8) & 0xFF00) | (header[1] & 0xFF);
  }

  public void parseHeader() {
    try {
      fileName = Conversions.fromByteArrayToString(header, DatagramProperties.FILENAMESIZE, DatagramProperties.FILENAMEOFFSET);
      fileType = Conversions.fromByteArrayToString(header, DatagramProperties.FILETYPESIZE, DatagramProperties.FILETYPEOFFSET);
      requestType = Requests.byValue((header[DatagramProperties.ACKNOWLEDGMENT_REQUESTOFFSET] & 0xF));
      acknowledgement = (header[DatagramProperties.ACKNOWLEDGMENT_REQUESTOFFSET] & 0x10) != 0;
      sequenceNumber = (header[DatagramProperties.SEQUENCE_NUMBEROFFSET] & 0xFF);
    } catch (InvalidRequestValue ignored) {
      // drop the packet
    }
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
