package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.exceptions.InvalidRequestValue;
import com.nedap.university.util.ChecksumCalculator;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
import java.util.regex.PatternSyntaxException;

public abstract class Packet {

  ChecksumCalculator checksumCalculator = new ChecksumCalculator();

  byte[] header;
  byte[] data;
  Requests requestType;
  boolean firstPacket;
  boolean acknowledgement;
  int sequenceNumber;
  String fileName;
  String fileType;

  // Create an empty packet
  public Packet() {
    header = new byte[HEADER_SIZE];
    requestType = Requests.EMPTY;
    acknowledgement = false;
    sequenceNumber = 0;
    fileName = "";
    fileType = "";
    setData(new byte[1]);
  }



  // Create ane empty packet with header and no data to send
  // use for acknowledge or list requests
  public Packet(Requests requestType, boolean firstPacket, boolean acknowledgement, int sequenceNumber) {
    header = new byte[HEADER_SIZE];
    this.requestType = requestType;
    this.firstPacket = firstPacket;
    this.acknowledgement = acknowledgement;
    this.sequenceNumber = sequenceNumber;
    this.fileName = "";
    this.fileType = "";
    setHeader();
    data = header;
  }

  // Create a packet with header and data to send
  public Packet(Requests requestType, boolean firstPacket, boolean acknowledgement, int sequenceNumber, String fileName, byte[] data) {
    header = new byte[HEADER_SIZE];
    this.data = new byte[Math.min(DatagramProperties.DATAGRAMSIZE, HEADER_SIZE + data.length)];
    this.requestType = requestType;
    this.firstPacket = firstPacket;
    this.acknowledgement = acknowledgement;
    this.sequenceNumber = sequenceNumber;
    try {
      if (Util.fileNameRequired(requestType)) {
        String[] file = fileName.split("\\.");
        this.fileName = file[0];
        this.fileType = file[1];
      } else {
        this.fileName = "";
        this.fileType = "";
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Splitting filename on . has failed, array index out of bounds!");
    } catch (PatternSyntaxException e) {
      System.out.println("Provided syntax" + fileName + " is incorrect!");
      System.out.println("Filename syntax should be <filename>.<filetype>");
    }
    setData(data);
  }

  private boolean validPacket() {
    return false;
  }

  public void setData(byte[] data) {
    setHeader();
    System.arraycopy(header, 0, this.data, 0, HEADER_SIZE);
    System.arraycopy(data, 0, this.data, HEADER_SIZE, data.length);
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
    header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] =
        (byte) (((firstPacket ? 0x01 : 0x00) << 5) | ((acknowledgement ? 0x01 : 0x00) << 4) | (requestType.getValue() & 0xFF)); ;
    header[DatagramProperties.SEQUENCE_NUMBEROFFSET] =
        (byte) (sequenceNumber & 0xFF);
  }

  public int getHeader() {
    return ((header[0] << 8) & 0xFF00) | (header[1] & 0xFF);
  }

  public void parseHeader() {
    try {
      fileName = Conversions.fromByteArrayToString(header, DatagramProperties.FILENAME_SIZE, DatagramProperties.FILENAME_OFFSET);
      fileType = Conversions.fromByteArrayToString(header, DatagramProperties.FILE_TYPE_SIZE, DatagramProperties.FILETYPEOFFSET);
      requestType = Requests.byValue((header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0xF));
      firstPacket = (header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x20) != 0;
      acknowledgement = (header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x10) != 0;
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
