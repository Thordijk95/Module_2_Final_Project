package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADERSIZE;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.InvalidRequestValue;
import com.nedap.university.util.ChecksumCalculator;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
import java.util.regex.PatternSyntaxException;

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
