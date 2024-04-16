package com.nedap.university.util;

public class DatagramProperties {

  public static int FILENAMESIZE = 20; // filenames are restricted to 20 bytes
  public static int FILETYPESIZE = 4;  // 4 bytes are used to encode the file type, extensions longer than 3 characters are not supported
  public static int ACKNOWLEDGMENT_REQUEST = 1; // 1 byte is used to store the acknowledgement and the requesttype of a packet
  public static int SEQUENCE_NUMBER = 1;  // 1 byte is used to store the sequence number of a packet, this limits the send window, however, SWS of 127 is sufficient
  public static int DATAGRAMSIZE = 100; // (int) Math.pow(2.0, 16.0); // fieldLength is a 16bit segment of the standard UDP header
  public static int HEADERSIZE = FILENAMESIZE + FILETYPESIZE + ACKNOWLEDGMENT_REQUEST + SEQUENCE_NUMBER;
  public static int DATASIZE = DATAGRAMSIZE-HEADERSIZE;

  public static int FILENAMEOFFSET = 0;
  public static int FILETYPEOFFSET = FILENAMEOFFSET + FILENAMESIZE;
  public static int ACKNOWLEDGMENT_REQUESTOFFSET = FILETYPEOFFSET + FILETYPESIZE;
  public static int SEQUENCE_NUMBEROFFSET = ACKNOWLEDGMENT_REQUESTOFFSET + ACKNOWLEDGMENT_REQUEST;
}
