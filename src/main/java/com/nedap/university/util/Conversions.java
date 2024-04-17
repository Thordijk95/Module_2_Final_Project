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

  public static void fromByteArrayToFileList(byte[] data, ArrayList<String> fileList) {

  }

}
