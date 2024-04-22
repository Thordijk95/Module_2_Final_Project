package com.nedap.university.CommandHandler;

import com.nedap.university.Communication.ReceiveWindow;
import com.nedap.university.Communication.Requests;
import com.nedap.university.Communication.Window;
import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.Packets.AckPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Communication.SlidingWindow;
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
  Window slidingWindow;
  Window receivingWindow;
  String storageDirectory;

  public abstractCommandHandler(DatagramSocket socket, String storageDirectory){
    this.socket = socket;
    util = new Util();
    this.storageDirectory = storageDirectory;
    slidingWindow = new SlidingWindow(storageDirectory);
    receivingWindow = new ReceiveWindow(storageDirectory);
    timeout = new Timeout(slidingWindow);
  }

  @Override
  public void executeCommand(String[] command, InetAddress address, int port, byte[] data) throws IncorrectArgumentException, IOException {
    if (command[0].toUpperCase().equals(Requests.LIST.name()) || command.length > 1) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList(address, port);
        case "UPLOAD" ->  upload(command[1], data);
        case "DOWNLOAD" -> download(command[1], address, port);
        case "REMOVE" -> remove(command[1]);
        case "RENAME" -> rename(command[1]);
        case "EMPTY" -> acknowledge(Requests.EMPTY, 0, address, port);
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
    socket.send(ackDatagramPacket);
  };
}
