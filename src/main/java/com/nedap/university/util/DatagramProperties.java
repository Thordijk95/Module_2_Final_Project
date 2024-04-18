package com.nedap.university.util;

public class DatagramProperties {

  public static int DATAGRAMSIZE = (int) Math.pow(2.0, 15.0); // fieldLength is a 16bit segment of the standard UDP header
  public static int UDP_HEADER_SIZE = 8; // Standard UDP header is 2 bytes sourceport, 2 bytes destination port, 2 bytes field length, 2 bytes checksum
  public static int FILENAME_SIZE = 20; // filenames are restricted to 20 bytes
  public static int FILE_TYPE_SIZE = 4;  // 4 bytes are used to encode the file type, extensions longer than 3 characters are not supported
  public static int ACKNOWLEDGMENT_REQUEST = 1; // 1 byte is used to store the acknowledgement and the requesttype of a packet
  public static int SEQUENCE_NUMBER = 1;  // 1 byte is used to store the sequence number of a packet, this limits the send window, however, SWS of 127 is sufficient
  public static int HEADER_SIZE = FILENAME_SIZE + FILE_TYPE_SIZE + ACKNOWLEDGMENT_REQUEST + SEQUENCE_NUMBER;
  public static int DATA_SIZE = DATAGRAMSIZE- HEADER_SIZE - UDP_HEADER_SIZE;

  public static int FILENAME_OFFSET = 0;
  public static int FILETYPEOFFSET = FILENAME_OFFSET + FILENAME_SIZE;
  public static int FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET = FILETYPEOFFSET + FILE_TYPE_SIZE;
  public static int SEQUENCE_NUMBEROFFSET = FIRSTPACKET_ACKNOWLEDGMENT_REQUESTOFFSET + ACKNOWLEDGMENT_REQUEST;

  public static String SEPARATOR = ";";
  public static long TIMEOUT = 5000; //milli seconds
}
