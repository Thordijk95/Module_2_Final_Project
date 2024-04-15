package com.nedap.university.server;

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

  static String storageDirectory = "/home/pi/PiFileServerStorageDirectory";

  int headerSize;

  public PiFileServer(int port, int headersize) throws SocketException {
      socket = new DatagramSocket(port);
      this.headerSize = headersize;
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Syntax error: PiFileServer <port>");
    }

    int port = Integer.parseInt(args[0]);
    try {
      PiFileServer server = new PiFileServer(port, 10);
      System.out.println("Started the Raspberry Pi File Server on port " + port);
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
      socket.receive(request);
      System.out.println("Received a request on the Raspberry Pi File Server");
      parseRequest(request);
    }


  }

  private Integer[] parseRequest(DatagramPacket request) throws IOException {

    byte[] header = request.getData();

    return new Integer[] {};
  }

}
