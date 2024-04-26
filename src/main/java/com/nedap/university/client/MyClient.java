package com.nedap.university.client;

import com.nedap.university.CommandHandler.ClientCommandHandler;
import com.nedap.university.CommandHandler.CommandHandler;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.nedap.university.exceptions.IncorrectArgumentException;

public class MyClient {

  CommandHandler clientCommandHandler;
  Util util;
  DatagramSocket socket;
  InetAddress SERVERADDRESS;
  int WELLKNOWNPORT;
  String STORAGEDIRECTORY;

  public MyClient(String[] args)
      throws SocketException, UnknownHostException, IncorrectArgumentException {
    if (args.length < 2) {
      throw new IncorrectArgumentException("Incorrect arguments: MyClient <host> <port>");
    }
    // TODO remove local host reference
    String hostName = args[0];
    WELLKNOWNPORT = Integer.parseInt(args[1]);
    SERVERADDRESS = InetAddress.getLocalHost();//InetAddress.getByName(args[0]);//InetAddress.getByName(hostName);
    STORAGEDIRECTORY = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files";

    System.out.println(hostName + ":" + WELLKNOWNPORT);
    socket = new DatagramSocket();

    util = new Util();
    clientCommandHandler = new ClientCommandHandler(socket, SERVERADDRESS, WELLKNOWNPORT, STORAGEDIRECTORY);

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
