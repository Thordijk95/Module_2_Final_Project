package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;
import java.net.InetAddress;

public class ConnectionPacket extends AbstractPacket {
  public ConnectionPacket(InetAddress address, int port) {
    setRequestType(Requests.CONNECT);
    setFirstPacket(false);
    setLastPacket(false);
    setAcknowledgement(false);
    setSequenceNumber(0);
    setAddress(address);
    setPort(port);
    setFileName("");
    setFileType("");
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
  }
}
