package com.nedap.university.server;

import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.util.CommandHandler.CommandHandler;
import com.nedap.university.util.Packet;
import com.nedap.university.util.CommandHandler.ServerCommandHandler;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PiFileServer {
  private DatagramSocket socket;
  private List<String> data = new ArrayList<String>();
  private Random random;

  static String storageDirectory = "/home/pi/PiFileServerStorageDirectory/";

  CommandHandler serverCommandHandler;
  Util util;
//

  public PiFileServer(int port, int headersize, int datagramsize) throws SocketException {
      socket = new DatagramSocket(port);
      serverCommandHandler = new ServerCommandHandler(socket, storageDirectory);
      util = new Util();
  }

  public static void main(Integer[] args) {
    if (args.length < 3) {
      System.out.println("Syntax error: PiFileServer <port>, <headersize> <datagramsize>");
    }

    try {
      PiFileServer server = new PiFileServer(args[0], args[1], args[2]);
      System.out.println("Started the Raspberry Pi File Server on port " + args[0]);
      server.service();
    } catch (SocketException e) {
      System.out.println("Socket unavailable");
      e.printStackTrace();
    } catch (IOException | IncorrectArgumentException e) {
      System.out.println("IO error");
      e.printStackTrace();
    }

  }

  private void service() throws IOException, IncorrectArgumentException {
    System.out.println("Starting the Raspberry Pi File Server Service");
    while(true) {
      DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
      System.out.println("Created a file buffer! Now trying to receive request");
      socket.receive(request);
      System.out.println("Received a request on the Raspberry Pi File Server");
      parseRequest(request);
    }


  }

  private void parseRequest(DatagramPacket request)
      throws IOException, IncorrectArgumentException {
    System.out.println("parsing request");
    Packet inboundPacket = new Packet(request.getData());
    if (inboundPacket.firstPacket) {
      System.out.println("Removing the file if it already exists");
      util.removeFile(storageDirectory + inboundPacket.fileName+"."+inboundPacket.fileType);
    }
    System.out.println(inboundPacket.getData().length + " bytes received");
    // Handle the packet
    System.out.println("Request = " + inboundPacket.getRequestType().toString());
    serverCommandHandler.executeCommand(new String[] {inboundPacket.getRequestType().toString(), inboundPacket.fileName+"."+inboundPacket.fileType},
        request.getAddress(), request.getPort(), request.getData());

    // acknowledge the packet
    Packet outboundPacket = new Packet(inboundPacket.getRequestType(),false, true, inboundPacket.sequenceNumber);
    DatagramPacket outboundDatagramPacket = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, request.getAddress(), request.getPort());
    System.out.println("Sending a response packet for the connection request");
    socket.send(outboundDatagramPacket);
    System.out.println("Checksum is incorrect!");
  }

}
