package com.nedap.university.util.CommandHandler;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.util.Packets.AckPacket;
import com.nedap.university.util.Packets.InterfacePacket;
import com.nedap.university.util.Window.SlidingWindow;
import com.nedap.university.util.Timeout;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public abstract class abstractCommandHandler implements CommandHandler {

  DatagramSocket socket;
  Util util;
  Timeout timeout;
  SlidingWindow slidingWindow;
  String storageDirectory;

  public abstractCommandHandler(DatagramSocket socket, String storageDirectory){
    this.socket = socket;
    util = new Util();
    slidingWindow = new SlidingWindow();
    timeout = new Timeout(slidingWindow);
    this.storageDirectory = storageDirectory;
  }

  @Override
  public void executeCommand(String[] command, InetAddress hostname, int port, byte[] data) throws IncorrectArgumentException, IOException {
    if (command[0].toUpperCase().equals(Requests.LIST.name()) || command.length > 1) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList(hostname, port);
        case "UPLOAD" ->  upload(command[1], data);
        case "DOWNLOAD" -> download(command[1], hostname, port);
        case "REMOVE" -> remove(command[1]);
        case "RENAME" -> rename(command[1]);
        case "EMPTY" -> acknowledge(Requests.EMPTY, 0, hostname, port);
        default->
            System.out.println("Unknown command: " + command[0]);
      }
    } else {
      throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
  }

  public void acknowledge(Requests request, int sequenceNumber, InetAddress hostname, int port) throws IOException {
    InterfacePacket ackPacket = new AckPacket(request, sequenceNumber);
    DatagramPacket ackDatagramPacket = new DatagramPacket(ackPacket.getData(), ackPacket.getData().length, hostname, port);
    System.out.println("Sending from acknowledge!!!");
    socket.send(ackDatagramPacket);
  };
}
