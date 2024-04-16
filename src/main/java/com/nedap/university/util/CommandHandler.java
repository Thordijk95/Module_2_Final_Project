package com.nedap.university.util;

import com.nedap.university.Requests;
import com.nedap.university.util.Packet;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.exceptions.IncorrectArgumentException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler {

  DatagramSocket socket;
  Util util;

  public CommandHandler(DatagramSocket socket){
    this.socket = socket;
    util = new Util();
  }

  public void executeCommand(String[] command, String hostname, int port) throws IncorrectArgumentException, IOException {
    if (command.length > 1 || command[0].equals(Requests.LIST.name())) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList();
        case "UPLOAD" ->  upload(command[1], hostname, port);
        case "DOWNLOAD" -> download(command[1]);
        case "REMOVE" -> remove(command[1]);
        case "RENAME" -> rename(command[1]);
        default->
            System.out.println("Unknown command: " + command[0]);
      }
    } else {
      throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
  }

  private void getList() {
    System.out.println("Retrieving list from server");
  }

  private void upload(String filePath, String hostname, int port) throws IOException {
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

      byte[] buffer = new byte[1024];
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      socket.receive(response);
      String quote = new String(buffer, 0, response.getLength());

      System.out.println(quote);
      System.out.println();
    }
  }

  private void download(String filePath) {
    System.out.println("Downloading file: " + filePath + " from server");
    // Construct the header
    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
    // add the file path as the data
  }

  private void remove(String filePath) {
    System.out.println("Removing file: " + filePath + " from server");
  }

  private void rename(String filePath) {
    System.out.println("Renaming file: " + filePath + " on server");
  }

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
