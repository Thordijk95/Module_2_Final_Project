package com.nedap.university.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.Packets.ErrorPacket;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public class ServerCommandHandler extends abstractCommandHandler{

  public ServerCommandHandler(DatagramSocket socket, String storageDirectory) {
    super(socket, storageDirectory);
  }

  @Override
  public void getList(InetAddress hostname, int port) throws IOException {
    System.out.println(storageDirectory);
    ArrayList<String> fileList = util.getFileList(storageDirectory);
    System.out.println(fileList);
    byte[] data = Conversions.fromArrayListToByteArray(fileList);
    ArrayList<byte[]> dataList = util.splitData(data);
    boolean firstPacket = true;
    int sequenceNumber = 0;
    for (byte[] packet : dataList) {
      packet = util.lastPacketInList(packet, dataList);
      InterfacePacket outboundPacket = new OutboundPacket(hostname, port, Requests.LIST, firstPacket, false, sequenceNumber,
          "", packet);
      DatagramPacket datagramPacket = new DatagramPacket(outboundPacket.getData(),
          outboundPacket.getData().length, hostname, port);
      socket.send(datagramPacket);
      firstPacket = false;

      byte[] buffer = new byte[1024];
      DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
      socket.receive(receivedPacket);
    }

  }

  // A file is uploaded to the serevr
  @Override
  public void upload(String filePath, byte[] data) throws IOException {
    util.safeFile(storageDirectory + filePath, data);
  }

  // A file is downloaded from the server
  @Override
  public void download(String fileName, InetAddress hostname, int port) throws IOException{
    System.out.println("Loading file: " + fileName);
    try {
      byte[] data = util.loadFile(storageDirectory + fileName);
      ArrayList<byte[]> dataList = util.splitData(data);
      boolean firstPacket = true;
      int sequenceNumber = 0;
      for (byte[] packet : dataList) {
        System.out.println("Creating packet " + (sequenceNumber+1) +":" + dataList.size() );
        packet = util.lastPacketInList(packet, dataList); // Adds a closure symbol ";;" to the data if it is the last packet
        System.out.println("1");
        InterfacePacket outboundPacket = new OutboundPacket(hostname, port, Requests.DOWNLOAD, firstPacket, false, sequenceNumber, fileName, packet);
        System.out.println("2");
        DatagramPacket outboundDatagramPacket = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, hostname, port);
        System.out.println("3");
        socket.send(outboundDatagramPacket);
        System.out.println("4");
        timeout.createTimer(outboundPacket, new Timer(), socket);
        System.out.println("Packet sent");
        firstPacket = false;
        sequenceNumber++;

        DatagramPacket ackDatagramPacket = new DatagramPacket(new byte[DATA_SIZE],
            DATA_SIZE);
        System.out.println("Waiting for ACK");
        socket.receive(ackDatagramPacket);
        InterfacePacket inboundPacket = new InboundPacket(ackDatagramPacket);
        if (inboundPacket.isAcknowledgement() && inboundPacket.getSequenceNumber()==outboundPacket.getSequenceNumber()) {
          System.out.println("Succesfully acknowledged a packet");
          slidingWindow.addAcknowledgedPacket(inboundPacket);
        }
      }

    } catch (IOException e) {
      System.out.println(e.getMessage());
      InterfacePacket errorPacket = new ErrorPacket();
    }

  }

  @Override
  public void remove(String filePath) throws IOException{

  }

  @Override
  public void rename(String filePath) throws IOException{

  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port)
      throws IOException, InterruptedException {
    return false;
  }
}
