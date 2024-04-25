package com.nedap.university.util;

import java.util.ArrayList;

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

  public static String fromByteArrayToString(byte[] data, int length, int offset) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if ((data[offset+i] & 0xFF) != 0x00) {
        byte character = (byte) ((data[offset + i] & 0xFF));
        String characterString = new String(Character.toChars(character));
        builder.append(characterString);
      }
    }
    return builder.toString();
  }

  public static String[] fromByteArrayToStringArray(byte[] data, String delimiter) {
    String allFileNames = fromByteArrayToString(data, data.length,0);
    return allFileNames.split(delimiter, -1);
  }

  public static byte[] fromFileListToByteArray(ArrayList<String> list) {
    StringBuilder builder = new StringBuilder();
    for (int i =0 ; i < list.size() ; i++) {
      builder.append(list.get(i) + DatagramProperties.SEPARATOR);
    }
    return builder.toString().getBytes();
  }

  public static byte[] fromDataListToByteArray(ArrayList<byte[]> list) {
    byte[] allBytes = new byte[0];
    for (byte[] bytes : list) {
      byte[] temp = allBytes;
      allBytes = new byte[bytes.length + allBytes.length];
      System.arraycopy(temp, 0, allBytes, 0, temp.length);
      System.arraycopy(bytes, 0, allBytes, temp.length, bytes.length);
    }
    return allBytes;
  }

  public static long fromByteArrayToLong(byte[] data, int length, int offset) {
    byte[] tmpData = new byte[length];
    long result = 0;
    System.arraycopy(data, offset, tmpData, 0, length);
    for (int i = 0; i < length; i++) {
      result = (result << 8) | (tmpData[i] & 0xFF);
    }
    return result;
  }

}
