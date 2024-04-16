package com.nedap.university.util;

public class Conversions {

  public Conversions() {}

  public int fromByteArrayToInt(byte[] data) {
    int tmp = 0;
    int result = 0;
    for (int i = 0; i < data.length; i++) {
      tmp = (tmp << 8) | (data[i] & 0xFF);
      result = tmp;
    }
    return result;
  }

  public int infoField(int requestType, boolean acknowledgement, int sequenceNumber) {
    return ((requestType << 9) | (acknowledgement ? 0x01 : 0x00) << 8) | (sequenceNumber) & 0xFFFF;
  }

}
