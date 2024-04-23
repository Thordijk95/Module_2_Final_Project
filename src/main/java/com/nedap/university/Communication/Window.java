package com.nedap.university.Communication;

import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.util.Timeout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public interface Window {

  void send(
      DatagramSocket socket, InetAddress address, int port, Requests requestType, boolean first, boolean last, boolean ack, int packetCounter,
      String filename, byte[] dataPacket) throws IOException;

  void sendPacket(DatagramSocket socket, InetAddress address, int port, InterfacePacket packet) throws IOException;

  InterfacePacket receive(DatagramSocket socket) throws IOException;

  void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType, ArrayList<byte[]> dataList, String filename) throws IOException;

  byte[] receiver(DatagramSocket socket, InetAddress address, int port, Requests requestsType) throws IOException;

  void addAcknowledgedPacket(InterfacePacket packet);

  void removeAcknowledgedPacket(InterfacePacket packet);

  ArrayList<InterfacePacket> getAcknowledgedPackets();

  void acknowledgePacket(DatagramSocket socket, InetAddress address,int port, InterfacePacket packet) throws IOException;

  boolean notYetAcknowledgedPacket(InterfacePacket packet);
}
