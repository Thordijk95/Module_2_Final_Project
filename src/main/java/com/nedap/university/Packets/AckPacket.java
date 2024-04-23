package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;

public class AckPacket extends AbstractPacket {

  // Create ane empty packet with header and no data to send
  // use for acknowledge or list requests
  public AckPacket(Requests requestType, int sequenceNumber) {
    setRequestType(requestType);
    setFirstPacket(false);
    setAcknowledgement(true);
    System.out.println("Setting sequence number for ackpacket to " + sequenceNumber);
    setSequenceNumber(sequenceNumber);
    setFileName("");
    setFileType("");
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
  }

}
