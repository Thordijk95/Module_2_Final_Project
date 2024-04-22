package com.nedap.university.Communication;

import com.nedap.university.Packets.AckPacket;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Timeout;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public abstract class AbstractWindow implements Window{

  Util util;
  Timeout timeout;
  String storageDirectory;

  ArrayList<InterfacePacket> acknowledgedPackets;

  public AbstractWindow(String directory) {
    util = new Util();
    timeout = new Timeout(this);
    acknowledgedPackets = new ArrayList<>();
    storageDirectory = directory;
  }

  public abstract void send(DatagramSocket socket, InetAddress address, int port, Requests requestType, boolean first, boolean ack, int packetCounter,
      String filename, byte[] dataPacket) throws IOException;

  public abstract void sendPacket(DatagramSocket socket, InetAddress address, int port, InterfacePacket packet) throws IOException;

  public abstract InterfacePacket receive(DatagramSocket socket) throws IOException;

  public abstract void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType, ArrayList<byte[]> dataList, String filename) throws IOException;

  public abstract byte[] receiver(DatagramSocket socket, InetAddress address, int port, Requests requestsType) throws IOException;

  public void addAcknowledgedPacket(InterfacePacket packet) {
    acknowledgedPackets.add(packet);
  }

  public void removeAcknowledgedPacket(InterfacePacket packet) {
    acknowledgedPackets.remove(packet);
  }

  public ArrayList<InterfacePacket> getAcknowledgedPackets() {
    return acknowledgedPackets;
  }

  public void acknowledgePacket(DatagramSocket socket, InetAddress address, int port, InterfacePacket packet) throws IOException {
    InterfacePacket ackPacket = new AckPacket(packet.getRequestType(), packet.getSequenceNumber());
    DatagramPacket ackDatagramPacket = new DatagramPacket(ackPacket.getData(), ackPacket.getData().length, address, port);
    socket.send(ackDatagramPacket);
    addAcknowledgedPacket(packet);
  }

  public boolean notYetAcknowledgedPacket(InterfacePacket packet) {
    return !acknowledgedPackets.contains(packet);
  }
}


