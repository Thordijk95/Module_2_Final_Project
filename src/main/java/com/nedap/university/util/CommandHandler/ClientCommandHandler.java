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

  DatagramSocket socket;
  Util util;

  public ClientCommandHandler(DatagramSocket socket){
    super(socket);
  }

  @Override
  public void getList() {
    System.out.println("Retrieving list from server");
  }

  @Override
  public void upload(String filePath, String hostname, int port) throws IOException {
    System.out.println("Uploading file: " + filePath + " to server");
    //byte[] data = loadFile(filePath);
    byte[] data = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    ArrayList<byte[]> dataList = util.splitData(data, DatagramProperties.DATASIZE);
    int packetCount = dataList.size();
    int packetCounter = 0;
    for (byte[] dataPacket : dataList) {
      Packet newPacket = new Packet(Requests.UPLOAD, false, packetCounter, dataPacket);
      DatagramPacket newDatagramPacket = new DatagramPacket(newPacket.getData(), newPacket.getData().length, InetAddress.getByName(hostname), port);
      System.out.println("Sending packet " + (packetCounter+1) + ": " + packetCount);
      socket.send(newDatagramPacket);
      packetCounter++;

      byte[] buffer = new byte[1024];
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      socket.receive(response);
      String quote = new String(buffer, 0, response.getLength());

      System.out.println(quote);
      System.out.println();
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
    socket.send(packet);
    Thread.sleep(500);
    byte[] response = new byte[512];
    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
    socket.receive(responsePacket);
    return responsePacket.getLength() > 0;
  }

}
