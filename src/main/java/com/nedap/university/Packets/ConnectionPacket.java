package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;

public class ConnectionPacket extends AbstractPacket {
  public ConnectionPacket() {
    setRequestType(Requests.CONNECT);
    setFirstPacket(false);
    setAcknowledgement(false);
    setSequenceNumber(0);
    setFileName("");
    setFileType("");
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
  }
}