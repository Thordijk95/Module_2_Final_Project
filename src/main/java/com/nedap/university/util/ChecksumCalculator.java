package com.nedap.university.util;

import com.nedap.university.Requests;

public class ChecksumCalculator {

  public int checksumCalculator(int sourcePort, int destinationPort, int fieldLength, int info) {
    // Split the words that form the header into bytes
    int[] headerWord = new int[] {
        (sourcePort & 0xFFFF), (destinationPort & 0xFFFF),
        (fieldLength & 0xFFFF), (info & 0xFFFF)
    };

    int checksum = headerWord[0] + headerWord[1];
    for (int i = 1; i < headerWord.length-1; i++) {
      checksum = checksum + headerWord[i+1];
      // Check overflow
      if ((checksum & 0x100) == 0x100) {
        checksum = checksum + 1;
      }
    }

    return ~checksum & 0xFFFF;
  }

  public byte[] onesComplement(int value) {
    return new byte[] { (byte) ((~value >>> 8) & 0xFF),  (byte) (~value & 0xFF)};
  }

  private byte MSByteFromInt(int value) {
    return (byte) ((value >>> 8) & 0xFF);
  }
  private byte LSByteFromInt(int value) {
    return (byte) (value & 0xFF);
  }

}
