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
  Window slidingWindow;
  Window receivingWindow;
  String storageDirectory;

  public abstractCommandHandler(DatagramSocket socket, String storageDirectory){
    this.socket = socket;
    util = new Util();
    this.storageDirectory = storageDirectory;
    slidingWindow = new SlidingWindow(storageDirectory);
    receivingWindow = new ReceiveWindow(storageDirectory);
  }

  @Override
  public void executeCommand(String[] command, InetAddress address, int port, byte[] data) throws IncorrectArgumentException, IOException {
    if (!(command[0].equals(Requests.LIST.toString())) && command.length == 2) {
      if(!Util.validFileName(command[1])) {
        throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
            + "Provided arguments are: " + Arrays.toString(command));
      }
    }
    if (command[0].equals(Requests.RENAME.toString()) && command.length == 3) {
      if(!Util.validFileName(command[2])) {
          throw new IncorrectArgumentException("Incorrect arguments: RENAME <filename> <filename>\n "
              + "Provided arguments are: " + Arrays.toString(command));
      }
    } else if (command[0].equals(Requests.RENAME.toString()) && command.length < 3){
      throw new IncorrectArgumentException("Incorrect arguments: RENAME <filename> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
    if (command[0].toUpperCase().equals(Requests.LIST.name()) || command.length > 1) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList(address, port);
        case "UPLOAD" ->  upload(command[1].toLowerCase(), address, port);
        case "DOWNLOAD" -> download(command[1].toLowerCase(), address, port);
        case "REMOVE" -> remove(command[1].toLowerCase());
        case "RENAME" -> rename(command[1].toLowerCase(), command[2].toLowerCase());
        case "EMPTY" -> {}
        default->
            System.out.println("Unknown command: " + command[0]);
      }
    } else {
      throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
  }

  public void acknowledge(InterfacePacket packet, InetAddress address, int port) throws IOException {
    slidingWindow.acknowledgePacket(socket, address, port, packet);
  };
}
