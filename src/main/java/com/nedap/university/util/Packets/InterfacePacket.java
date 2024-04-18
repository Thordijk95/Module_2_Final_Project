package com.nedap.university.util.Packets;

import com.nedap.university.Requests;

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

  void parseHeader();

  boolean isValidPacket();
}
