package com.nedap.university.client;

import com.nedap.university.util.PacketConstructor;
import com.nedap.university.util.PacketParser;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.nedap.university.Exceptions.IncorrectArgumentException;

public class MyClient {

  PacketParser packetParser;
  PacketConstructor packetConstructor;
  InetAddress address;
  DatagramSocket socket;

  public MyClient(String[] args)
      throws SocketException, UnknownHostException, IncorrectArgumentException {
    if (args.length < 2) {
      throw new IncorrectArgumentException("Incorrect arguments: MyClient <host> <port>");
    }

    String hostName = args[0];
    int port = Integer.parseInt(args[1]);
    address = InetAddress.getByName(hostName);
    System.out.println(hostName + ":" + port);
    socket = new DatagramSocket(port);
    packetParser = new PacketParser();
    packetConstructor = new PacketConstructor();
  }

  public void executeCommand(String[] command) {

    switch (command[0].toUpperCase()) {
      case "LIST" : getList();
      case "UPLOAD" : upload(command[1]);
      case "DOWNLOAD" : download(command[1]);
      case "REMOVE" : remove(command[1]);
      case "RENAME" : rename(command[1]);
      default: {
        System.out.println("Unknown command: " + command[0]);
      }
    }

  }

  private void getList() {

  }


  private void upload(String filePath) {}

  private void download(String filePath) {
    // Construct the header
    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
    // add the file path as the data
  }

  private void remove(String filePath) {
  }

  private void rename(String filePath) {}
//  private byte[] loadFile(String filename) {
//
//  }



}
