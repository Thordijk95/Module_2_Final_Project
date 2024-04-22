package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;

public class ClosingPacket extends AbstractPacket {

  public ClosingPacket(Requests requestType) {
    setRequestType(requestType);
    setFirstPacket(false);
    setLastPacket(true);
    setAcknowledgement(true);
    setSequenceNumber(255);
    setFileName("");
    setFileType("");
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
  }

}
