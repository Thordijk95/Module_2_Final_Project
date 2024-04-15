package com.nedap.university.server;

import com.nedap.university.util.PacketParser;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PiFileServer {
  private DatagramSocket socket;
  private List<String> data = new ArrayList<String>();
  private Random random;

  static String storageDirectory = "/home/pi/PiFileServerStorageDirectory";

  static int headerSize;
  static int datagramSize;

  PacketParser packetParser;

  public PiFileServer(int port, int headersize, int datagramsize) throws SocketException {
      socket = new DatagramSocket(port);
      headerSize = headersize;
      datagramSize = datagramsize;
      packetParser = new PacketParser();
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
    } catch (IOException e) {
      System.out.println("IO error");
      e.printStackTrace();
    }

  }

  private void service() throws IOException {
    System.out.println("Starting the Raspberry Pi File Server Service");
    while(true) {
      DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
      System.out.println("Created a file buffer! Now trying to receive request");
      socket.receive(request);
      System.out.println("Received a request on the Raspberry Pi File Server");
      parseRequest(request);
    }


  }

  private Integer[] parseRequest(DatagramPacket request) throws IOException {
    byte[] data = request.getData();
    System.out.println(Arrays.toString(data));
    if (packetParser.evaluateChecksum(data, headerSize)) {
      System.out.println("Checksum is correct!");
    } else {
      System.out.println(data.length + " bytes received");
      System.out.println("Sending a response packet for the connection request");
      socket.send(new DatagramPacket(data, request.getLength(), request.getAddress(), request.getPort()));
      System.out.println("Checksum is incorrect!");
    }

    return new Integer[] {};
  }

}
