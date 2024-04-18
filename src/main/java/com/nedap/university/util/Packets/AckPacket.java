package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Requests;

public class AckPacket extends AbstractPacket {

  // Create ane empty packet with header and no data to send
  // use for acknowledge or list requests
  public AckPacket(Requests requestType, int sequenceNumber) {
    setRequestType(requestType);
    setFirstPacket(false);
    setAcknowledgement(true);
    setSequenceNumber(sequenceNumber);
    setFileName("");
    setFileType("");
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
  }

}
