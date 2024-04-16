package com.nedap.university.client;

import com.nedap.university.util.CommandHandler.ClientCommandHandler;
import com.nedap.university.util.CommandHandler.CommandHandler;
import com.nedap.university.util.PacketConstructor;
import com.nedap.university.util.PacketParser;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.nedap.university.exceptions.IncorrectArgumentException;

public class MyClient {

  CommandHandler clientCommandHandler;
  PacketParser packetParser;
  PacketConstructor packetConstructor;
  Util util;
  DatagramSocket socket;
  InetAddress SERVERADDRESS;
  InetAddress CLIENTADDRESS;
  int WELLKNOWNPORT;

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
    clientCommandHandler = new ClientCommandHandler(socket);

    try {
      if(clientCommandHandler.testConnectionAtRunTime(SERVERADDRESS, WELLKNOWNPORT)) {
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
}
