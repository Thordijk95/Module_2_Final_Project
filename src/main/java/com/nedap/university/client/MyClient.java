package com.nedap.university.client;

import com.nedap.university.Requests;
import com.nedap.university.util.CommandHandler;
import com.nedap.university.util.PacketConstructor;
import com.nedap.university.util.PacketParser;
import com.nedap.university.util.Util;
import com.nedap.university.util.DatagramProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.nedap.university.exceptions.IncorrectArgumentException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyClient {

  CommandHandler commandHandler;
  PacketParser packetParser;
  PacketConstructor packetConstructor;
  Util util;
  DatagramSocket socket;
  InetAddress SERVERADDRESS;
  InetAddress CLIENTADDRESS;
  int WELLKNOWNPORT;

  int HEADERSIZE;
  int DATASIZE;

  public MyClient(String[] args)
      throws SocketException, UnknownHostException, IncorrectArgumentException {
    if (args.length < 2) {
      throw new IncorrectArgumentException("Incorrect arguments: MyClient <host> <port>");
    }

    String hostName = args[0];
    WELLKNOWNPORT = Integer.parseInt(args[1]);
    SERVERADDRESS = InetAddress.getByName(hostName);

    System.out.println(hostName + ":" + WELLKNOWNPORT);
    socket = new DatagramSocket();

    packetParser = new PacketParser();
    packetConstructor = new PacketConstructor();
    util = new Util();
    commandHandler = new CommandHandler(socket);

    try {
      if(commandHandler.testConnectionAtRunTime(SERVERADDRESS, WELLKNOWNPORT)) {
        System.out.println("Successfully connected to " + hostName + ":" + WELLKNOWNPORT);
      } else {
        System.out.println("Expected an exception, guess not?");
      }
    } catch (IOException e) {
      System.out.println("Failed to connect to " + hostName + ":" + WELLKNOWNPORT);
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.out.println("Timeout failed");
    }
  }

  public void executeCommand(String[] command) throws IncorrectArgumentException, IOException {
    if (command.length > 1 || command[0].equals(Requests.LIST.name())) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList();
        case "UPLOAD" ->  upload(command[1]);
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

  private void upload(String filePath) throws IOException {
    System.out.println("Uploading file: " + filePath + " to server");
    //byte[] data = loadFile(filePath);
    byte[] data = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    ArrayList<byte[]> dataList = util.splitData(data, DATASIZE);
    int dataSize = dataList.size();
    int packetCounter = 0;
    for (byte[] dataPacket : dataList) {
      byte[] datagram = packetConstructor.constructPacket(HEADERSIZE, CLIENTADDRESS.hashCode(), SERVERADDRESS.hashCode(), dataPacket.length, Requests.UPLOAD.getValue(), false, packetCounter, dataPacket);
      packetParser.evaluateChecksum(dataPacket, HEADERSIZE);
      System.out.println("Sending datagramPacket " + (packetCounter+1) + ": " + dataSize);
      socket.send(new DatagramPacket(datagram, datagram.length, SERVERADDRESS, WELLKNOWNPORT));

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

  private boolean testConnectionAtRunTime() throws IOException, InterruptedException {
    DatagramPacket packet = new DatagramPacket(new byte[1], 1, SERVERADDRESS, WELLKNOWNPORT);
    System.out.println("Sending data gram");
    socket.send(packet);
    Thread.sleep(500);
    byte[] response = new byte[512];
    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
    socket.receive(responsePacket);
    return responsePacket.getLength() > 0;
  }
}
