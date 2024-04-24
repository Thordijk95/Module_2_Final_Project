package com.nedap.university.Communication;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Packets.AckPacket;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.util.Timeout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class ReceiveWindow extends AbstractWindow {

  Timeout timeout;

  ArrayList<InterfacePacket> receiveWindow;

  int RWS = (int) Math.pow((DatagramProperties.SEQUENCE_NUMBER_SIZE * 2),
      (DatagramProperties.SEQUENCE_NUMBER_SIZE * 8)) / 2;
  int LFR = 0;
  int LAF = LFR + RWS;

  public ReceiveWindow(String directory) {
    super(directory);
    receiveWindow = new ArrayList<>();
    timeout = new Timeout(this);
  }

  @Override
  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      boolean first, boolean last, boolean ack, int packetCounter, String filename,
      byte[] dataPacket) throws IOException {
    // Should not be used from receivewindow
  }

  @Override
  public void sendPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    // should not be used from receive window
  }

  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      ArrayList<byte[]> dataList, String filename) throws IOException {
    // Should not be used from receive window
  }

  @Override
  public InterfacePacket receive(DatagramSocket socket) throws IOException {
    DatagramPacket inboundDatagram = new DatagramPacket(new byte[DatagramProperties.DATAGRAMSIZE],
        DatagramProperties.DATAGRAMSIZE);
    socket.receive(inboundDatagram);
    return new InboundPacket(inboundDatagram);
  }

  @Override
  public byte[] receiver(DatagramSocket socket, InetAddress address, int port,
      Requests requestsType) throws IOException {
    ArrayList<byte[]> dataList = new ArrayList<byte[]>();
    // start the receiver
    System.out.println("Starting receiver!!!");
    while (true) {
      // receive the next packet
      InterfacePacket downloadPacket = receive(socket);
      if (verifyNewPacket(socket, address, port, downloadPacket)) {
        System.out.println("Received " + downloadPacket.getData().length +" bytes of data");
        byte[] data = downloadPacket.getData();
        dataList.add(data);
        if (downloadPacket.isLastPacket()) {
          return Conversions.fromDataListToByteArray(dataList);
        }
      }
    }
  }

  @Override
  public boolean verifyAcknowledgement(InterfacePacket ackPacket) {
    System.out.println("This method should not be used from the receiver class!!");
    return false;
  }

  @Override
  public boolean verifyNewPacket(DatagramSocket socket, InetAddress address, int port,
      InterfacePacket packet) throws IOException {
    // Check if packet is valid and not already acknowledged (e.g. acknowledgement was lost, this is resend)
    if (packet.isValidPacket() && !getAcknowledgedPackets().contains(packet)) {
      acknowledgePacket(socket, address, port, packet);
      LFR = packet.getSequenceNumber();
      return true;
    } else if (packet.isValidPacket() && getAcknowledgedPackets().contains(packet)) {
      // Packet has already been processed and acknowledged but acknowledgement was apparently lost
      // resend the acknowledgement but do not use the data
      acknowledgePacket(socket, address, port, packet);
    } else {
      System.out.println("Dropped the packet");
    }
    return false;
  }

}