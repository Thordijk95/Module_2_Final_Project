package com.nedap.university.Packets;

import com.nedap.university.Communication.Requests;

public class EmptyPacket extends AbstractPacket {
  // Create an empty packet
  public EmptyPacket() {
    setRequestType(Requests.EMPTY);
    setFirstPacket(false);
    setAcknowledgement(false);
    setSequenceNumber(0);
    setFileName("");
    setFileType("");
    setData(new byte[1], 0, 0, 1);
  }

}
