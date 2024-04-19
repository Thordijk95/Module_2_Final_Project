package com.nedap.university.Packets;

import com.nedap.university.Communication.Requests;

public class ErrorPacket extends AbstractPacket{

  public ErrorPacket() {
    setRequestType(Requests.ERROR);
    setFirstPacket(false);
    setAcknowledgement(false);
    setSequenceNumber(0);
    setFileName("");
    setFileType("");
    setData(new byte[1], 0, 0, 1);
  }

}
