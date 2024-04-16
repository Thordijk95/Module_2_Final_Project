package com.nedap.university.util;

public class DatagramProperties {
  public static int DATAGRAMSIZE = 100; // (int) Math.pow(2.0, 16.0); // fieldLength is a 16bit segment of the standard UDP header
  public static int HEADERSIZE = 2;
  public static int DATASIZE = DATAGRAMSIZE-HEADERSIZE;

}
