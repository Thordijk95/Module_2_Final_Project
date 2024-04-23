package com.nedap.university.Packets;

import com.nedap.university.Communication.Requests;

public class ErrorPacket extends AbstractPacket{

  public ErrorPacket(String message) {
    setRequestType(Requests.ERROR);
    setFirstPacket(false);
    setAcknowledgement(false);
    setSequenceNumber(0);
    setFileName("");
    setFileType("");
    setData(message.getBytes(), 0, 0, 1);
  }

}
