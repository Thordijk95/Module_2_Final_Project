package com.nedap.university.util.CommandHandler;

import com.nedap.university.Requests;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Packet;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientCommandHandler extends abstractCommandHandler{

  public ClientCommandHandler(DatagramSocket socket){
    super(socket);
  }

  @Override
  public void getList() {
    System.out.println("Retrieving list from server");
  }

  @Override
  public void upload(String filePath, InetAddress hostname, int port, byte[] ignored_data) throws IOException {
    // TODO remove static file path
    filePath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf";
    System.out.println("Uploading file: " + filePath + " to server");
    byte[] data = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    ArrayList<byte[]> dataList = util.splitData(data, DatagramProperties.DATASIZE);
    int packetCount = dataList.size();
    int packetCounter = 0;
    String filename = filePath.split("/")[filePath.split("/").length - 1];
    for (byte[] dataPacket : dataList) {
      Packet newPacket = new Packet(Requests.UPLOAD, false, packetCounter, filename, dataPacket);
      DatagramPacket newDatagramPacket = new DatagramPacket(newPacket.getData(), newPacket.getData().length, hostname, port);
      System.out.println("Sending packet " + (packetCounter+1) + ": " + packetCount);
      socket.send(newDatagramPacket);
      packetCounter++;

      byte[] buffer = new byte[1024];
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      socket.receive(response);
      Packet inboundPacket = new Packet(response.getData());
      if (inboundPacket.acknowledgement && inboundPacket.sequenceNumber==newPacket.sequenceNumber) {
        System.out.println("Succesfully acknowledged a packet");
      } else {
        System.out.println("fail!");
        System.out.println("acknowledgement: " + inboundPacket.acknowledgement);
        System.out.println("Last packet send sequence number: " + newPacket.sequenceNumber);
        System.out.println("Last packet received sequence number: " + inboundPacket.sequenceNumber);
      }
    }
  }
  @Override
  public void download(String filePath) {
    System.out.println("Downloading file: " + filePath + " from server");
    // Construct the header
    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
    // add the file path as the data
  }

  @Override
  public void remove(String filePath) {
    System.out.println("Removing file: " + filePath + " from server");
  }

  @Override
  public void rename(String filePath) {
    System.out.println("Renaming file: " + filePath + " on server");
  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException {
    DatagramPacket packet = new DatagramPacket(new byte[1], 1, hostname, port);
    System.out.println("Sending data gram");
    super.socket.send(packet);
    Thread.sleep(500);
    byte[] response = new byte[512];
    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
    socket.receive(responsePacket);
    return responsePacket.getLength() > 0;
  }

}
