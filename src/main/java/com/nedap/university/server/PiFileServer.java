package com.nedap.university.server;

import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.CommandHandler.CommandHandler;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.CommandHandler.ServerCommandHandler;
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
      System.out.println(e.getMessage());
    } catch (IOException | IncorrectArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  private void service() throws IOException, IncorrectArgumentException {
    System.out.println("Starting the Raspberry Pi File Server Service");
    while(true) {
      DatagramPacket request = new DatagramPacket(new byte[DatagramProperties.DATAGRAMSIZE], DatagramProperties.DATAGRAMSIZE);
      System.out.println("Created a file buffer! Now trying to receive request");
      socket.receive(request);
      System.out.println("Received a request on the Raspberry Pi File Server");
      parseRequest(request);
    }
  }

  private void parseRequest(DatagramPacket request)
      throws IOException, IncorrectArgumentException {
    InterfacePacket inboundPacket = new InboundPacket(request);

    if (inboundPacket.isValidPacket()) {
      // Acknowledge the packet
      System.out.println("Sending acknowledgement!");
      serverCommandHandler.acknowledge(inboundPacket.getRequestType(), inboundPacket.getSequenceNumber(), request.getAddress(), request.getPort());;

      if (inboundPacket.isFirstPacket() && !(inboundPacket.getFileName().isEmpty() || inboundPacket.getFileType().isEmpty())) {
        util.removeFile(storageDirectory + inboundPacket.getFileName()+"."+inboundPacket.getFileType());
      }
      // Handle the packet
      System.out.println("Received " + inboundPacket.getData().length + " bytes of data");
      System.out.println("executing request: " + inboundPacket.getRequestType().toString());
      serverCommandHandler.executeCommand(new String[] {inboundPacket.getRequestType().toString(), inboundPacket.getFileName()+"."+inboundPacket.getFileType()},
          request.getAddress(), request.getPort(), inboundPacket.getData());

    } else {
      System.out.println("Dropped the packet!");
    }

  }

}
