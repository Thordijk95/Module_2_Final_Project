package com.nedap.university.util.CommandHandler;

import com.nedap.university.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.Packets.AckPacket;
import com.nedap.university.util.Packets.ErrorPacket;
import com.nedap.university.util.Packets.InterfacePacket;
import com.nedap.university.util.Packets.OutboundPacket;
import com.nedap.university.util.Packets.Packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.crypto.Data;

public class ServerCommandHandler extends abstractCommandHandler{

  String storageDirectory;
  public ServerCommandHandler(DatagramSocket socket, String storageDirectory) {
    super(socket);
    this.storageDirectory = storageDirectory;
  }

  @Override
  public void getList(InetAddress hostname, int port) throws IOException {
    ArrayList<String> fileList = util.getFileList(storageDirectory);
    System.out.println(fileList);
    byte[] data = Conversions.fromArrayListToByteArray(fileList);
    ArrayList<byte[]> dataList = util.splitData(data);
    boolean firstPacket = true;
    int sequenceNumber = 0;
    for (byte[] packet : dataList) {
      System.out.println("Creating packet");
      InterfacePacket outboundPacket = new OutboundPacket(Requests.LIST, firstPacket, true, sequenceNumber,
          "", packet);
      System.out.println(outboundPacket.getRequestType());
      System.out.println(Arrays.toString(outboundPacket.getData()));
      System.out.println("Creating datagram");
      DatagramPacket datagramPacket = new DatagramPacket(outboundPacket.getData(),
          outboundPacket.getData().length, hostname, port);
      System.out.println("Sending datagram");
      socket.send(datagramPacket);
      firstPacket = false;

      byte[] buffer = new byte[1024];
      DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
      socket.receive(receivedPacket);
      System.out.println("Received packet : " + new String(receivedPacket.getData()));
    }

  }

  // A file is uploaded to the serevr
  @Override
  public void upload(String filePath, byte[] data) throws IOException {
    System.out.println("Saving inbound data on the server at location " + storageDirectory + filePath);
    util.safeFile(storageDirectory + filePath, data);
  }

  // A file is downloaded from the server
  @Override
  public void download(String filePath, InetAddress hostname, int port) throws IOException{
    System.out.println("Loading file: " + filePath);
    try {
      util.loadFile(storageDirectory + filePath);

    } catch (IOException e) {
      System.out.println("File not found in: " + storageDirectory + filePath);
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
