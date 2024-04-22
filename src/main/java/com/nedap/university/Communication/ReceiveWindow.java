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

public class ReceiveWindow extends AbstractWindow{

  Timeout timeout;

  ArrayList<InterfacePacket> receiveWindow;

  public ReceiveWindow(String directory) {
    super(directory);
    receiveWindow = new ArrayList<>();
    timeout = new Timeout(this);
  }

  @Override
  public void send(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      boolean first, boolean ack, int packetCounter, String filename, byte[] dataPacket) throws IOException {
    // Should not be used from receivewindow
  }

  @Override
  public void sendPacket(DatagramSocket socket, InetAddress address, int port, InterfacePacket packet) throws IOException {
    // should not be used from receive window
  }

  @Override
  public InterfacePacket receive(DatagramSocket socket) throws IOException {
      DatagramPacket inboundDatagram = new DatagramPacket(new byte[DatagramProperties.DATA_SIZE], DatagramProperties.DATA_SIZE);
      socket.receive(inboundDatagram);
      return new InboundPacket(inboundDatagram);
  }

  @Override
  public void sender(DatagramSocket socket, InetAddress address, int port, Requests requestType,
      ArrayList<byte[]> dataList, String filename) throws IOException {
    // Should not be used from receive window
  }

  @Override
  public byte[] receiver(DatagramSocket socket, InetAddress address, int port, Requests requestsType) throws IOException {
    ArrayList<byte[]> dataList = new ArrayList<byte[]>();
    // start the receiver
    while(true) {
      // receive the next packet
      InterfacePacket downloadPacket = receive(socket);
      // Check if packet is valid and not already acknowledged (e.g. acknowledgement was lost, this is resend)
      if (downloadPacket.isValidPacket() && notYetAcknowledgedPacket(downloadPacket)) {
        acknowledgePacket(socket, address, port, downloadPacket);
        if (downloadPacket.isLastPacket()) {
          return Conversions.fromDataListToByteArray(dataList);
        }
        System.out.println("Received data packet");
        byte[] data = downloadPacket.getData();
        dataList.add(data);


      } else if (downloadPacket.isValidPacket() && !notYetAcknowledgedPacket(downloadPacket)) {
        // Packet has already been processed and acknowledged but acknowledgement was apparently lost
        // resend the acknowledgement but do not use the data
        acknowledgePacket(socket, address, port, downloadPacket);
      }
    }
  }
}