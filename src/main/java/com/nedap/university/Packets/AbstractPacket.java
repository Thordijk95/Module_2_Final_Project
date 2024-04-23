package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.exceptions.InvalidRequestValue;
import com.nedap.university.util.ChecksumCalculator;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import java.net.InetAddress;

public abstract class AbstractPacket implements InterfacePacket{
  ChecksumCalculator checksumCalculator = new ChecksumCalculator();

  private byte[] header;
  private byte[] data;
  private Requests requestType;
  private boolean firstPacket;
  private boolean lastPacket;
  private boolean acknowledgement;
  private int sequenceNumber;
  private String fileName;
  private String fileType;

  private InetAddress address;
  private int port;

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
    header = new byte[HEADER_SIZE];
    System.arraycopy(data, 0, header, 0, HEADER_SIZE);
  }

  @Override
  public void constructHeader() {
    header = new byte[HEADER_SIZE];
    // byte 0 - 19 = filename
    if (fileName != null) {
      byte[] fileNameBytes = fileName.getBytes();
      for (int i = 0; i < fileNameBytes.length; i++) {
        header[i] = fileNameBytes[i];
      }
    }
    if (fileType != null) {
      // byte 20-23 = file type
      byte[] fileTypeBytes = fileType.getBytes();
      for (int i = 0; i < fileTypeBytes.length; i++) {
        header[DatagramProperties.FILETYPEOFFSET + i] = fileTypeBytes[i];
      }
    }
    // byte
    header[DatagramProperties.FIRSTPACKET_LASTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] =
        (byte) (((firstPacket ? 0x01 : 0x00) << 6) | ((lastPacket ? 0x01 : 0x00) << 5) | ((acknowledgement ? 0x01 : 0x00) << 4) | (requestType.getValue() & 0xFF)); ;
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
  public void setLastPacket(boolean lastPacket) {
    this.lastPacket = lastPacket;
  };

  @Override
  public boolean isLastPacket() {
    return lastPacket;
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
  public void setAddress(InetAddress address) {
    this.address = address;
  }

  @Override
  public InetAddress getAddress() {
    return address;
  };

  @Override
  public void setPort(int port) {
    this.port = port;
  };

  @Override
  public int getPort(){
    return port;
  };

  @Override
  public void parseHeader() {
    try {
      fileName = Conversions.fromByteArrayToString(header, DatagramProperties.FILENAME_SIZE, DatagramProperties.FILENAME_OFFSET);
      fileType = Conversions.fromByteArrayToString(header, DatagramProperties.FILE_TYPE_SIZE, DatagramProperties.FILETYPEOFFSET);
      requestType = Requests.byValue((header[DatagramProperties.FIRSTPACKET_LASTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0xF));
      firstPacket = (header[DatagramProperties.FIRSTPACKET_LASTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x40) != 0;
      lastPacket = (header[DatagramProperties.FIRSTPACKET_LASTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x20) != 0;
      acknowledgement = (header[DatagramProperties.FIRSTPACKET_LASTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET] & 0x10) != 0;
      sequenceNumber = (header[DatagramProperties.SEQUENCE_NUMBEROFFSET] & 0xFF);
      System.out.println("Parsed header, seqnum : " + sequenceNumber);
    } catch (InvalidRequestValue ignored) {
      System.out.println("cannot parse the header!");
      // drop the packet
    }
  }

  @Override
  public boolean isValidPacket() {
    System.out.println("validate packet");
    if (!Requests.validRequest(requestType.toString())) {
      System.out.println("Request not valid: " + requestType.toString());
      return false;
    }
    if (sequenceNumber < 0 || sequenceNumber > 0xFFFF) {
      System.out.println("Invalid sequence number: " + sequenceNumber);
      return false;
    }
    if (firstPacket && (sequenceNumber != 0 || acknowledgement)) {
      System.out.println("Invalid first packet: " + firstPacket);
      System.out.println("Sequence number: " + sequenceNumber);
      System.out.println("Acknowledgement: " + acknowledgement);
      return false;
    }
    return true;
  };
}
