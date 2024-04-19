package com.nedap.university.Packets;

import com.nedap.university.Communication.Requests;
import java.net.InetAddress;

public interface InterfacePacket {

  void setData(byte[] source, int sourcePosition, int destinationPosition, int length);

  byte[] getData();

  void setHeader(byte[] data);

  void constructHeader();

  byte[] getHeader();

  void setRequestType(Requests requestsType);

  Requests getRequestType();

  void setFirstPacket(boolean firstPacket);

  boolean isFirstPacket();

  void setAcknowledgement(boolean acknowledgement);

  boolean isAcknowledgement();

  void setSequenceNumber(int sequenceNumber);

  int getSequenceNumber();

  void setFileName(String fileName);

  String getFileName();

  void setFileType(String fileType);

  String getFileType();

  void setAddress(InetAddress address);

  InetAddress getAddress();

  void setPort(int port);

  int getPort();

  void parseHeader();

  boolean isValidPacket();
}
