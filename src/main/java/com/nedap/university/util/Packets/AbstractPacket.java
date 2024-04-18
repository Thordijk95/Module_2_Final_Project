package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADERSIZE;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.InvalidRequestValue;
import com.nedap.university.util.ChecksumCalculator;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
import java.util.regex.PatternSyntaxException;

public abstract class AbstractPacket implements InterfacePacket{
  ChecksumCalculator checksumCalculator = new ChecksumCalculator();

  private byte[] header;
  private byte[] data;
  private Requests requestType;
  private boolean firstPacket;
  private boolean acknowledgement;
  private int sequenceNumber;
  private String fileName;
  private String fileType;

  private boolean validPacket() {
    return false;
  }

  @Override
  public void setData(byte[] source, int sourcePosition, int destinationPosition, int length) {
    if (data==null) {
      data = new byte[length];
    } else {
      byte[] tmp = data;
      data = new byte[length+tmp.length];
      System.arraycopy(tmp, 0, data, 0, tmp.length);
    }
    System.arraycopy(source, sourcePosition, this.data, destinationPosition, length);
  }
  @Override
  public byte[] getData() {
    return data;
  }

  @Override
  public void setHeader(byte[] data) {
    header = new byte[HEADERSIZE];
    System.arraycopy(data, 0, header, 0, HEADERSIZE);
  }

  @Override
  public void constructHeader() {
    header = new byte[HEADERSIZE];
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
  @Override
  public byte[] getHeader() {
    return header;
  }

  @Override
  public void setRequestType(Requests requestsType) {
    this.requestType = requestsType;
  };

  @Override
  public Requests getRequestType() {
    return requestType;
  };

  @Override
  public void setFirstPacket(boolean firstPacket) {
    this.firstPacket = firstPacket;
  };

  @Override
  public boolean isFirstPacket() {
    return firstPacket;
  };

  @Override
  public void setAcknowledgement(boolean acknowledgement) {
    this.acknowledgement = acknowledgement;
  };

  @Override
  public boolean isAcknowledgement() {
    return acknowledgement;
  };

  @Override
  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  };

  @Override
  public int getSequenceNumber() {
    return sequenceNumber;
  };

  @Override
  public void setFileName(String fileName) {
    this.fileName = fileName;
  };

  @Override
  public String getFileName() {
    return fileName;
  };

  @Override
  public void setFileType(String fileType) {
    this.fileType = fileType;
  };

  @Override
  public String getFileType() {
    return fileType;
  };

  @Override
  public void parseHeader() {
    try {
      fileName = Conversions.fromByteArrayToString(header, DatagramProperties.FILENAMESIZE, DatagramProperties.FILENAMEOFFSET);
      fileType = Conversions.fromByteArrayToString(header, DatagramProperties.FILETYPESIZE, DatagramProperties.FILETYPEOFFSET);
      requestType = Requests.byValue((header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0xF));
      firstPacket = (header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x20) != 0;
      acknowledgement = (header[DatagramProperties.FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x10) != 0;
      sequenceNumber = (header[DatagramProperties.SEQUENCE_NUMBEROFFSET] & 0xFF);
    } catch (InvalidRequestValue ignored) {
      System.out.println("cannot parse the header!");
      // drop the packet
    }
  }

  @Override
  public boolean isValidPacket() {
    parseHeader();
    if (!Requests.validRequest(requestType.toString())) {
      System.out.println("Request not valid: " + requestType.toString());
      return false;
    }
    if (sequenceNumber < 0 || sequenceNumber > 0xFFFF) {
      System.out.println("Invalid sequence number: " + sequenceNumber);
      return false;
    }
    if (firstPacket && sequenceNumber != 0 || acknowledgement) {
      System.out.println("Invalid first packet: " + firstPacket);
      System.out.println("Sequence number: " + sequenceNumber);
      System.out.println("Acknowledgemetn: " + acknowledgement);
      return false;
    }
    return true;
  };
}
