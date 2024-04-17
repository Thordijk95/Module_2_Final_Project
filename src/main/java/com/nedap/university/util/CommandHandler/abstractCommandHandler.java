package com.nedap.university.util.CommandHandler;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public abstract class abstractCommandHandler implements CommandHandler {

  DatagramSocket socket;
  Util util;

  public abstractCommandHandler(DatagramSocket socket){
    this.socket = socket;
    util = new Util();
  }

  @Override
  public void executeCommand(String[] command, InetAddress hostname, int port, byte[] data) throws IncorrectArgumentException, IOException {
    if (command[0].toUpperCase().equals(Requests.LIST.name()) || command.length > 1) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList();
        case "UPLOAD" ->  upload(command[1], data);
        case "DOWNLOAD" -> download(command[1]);
        case "REMOVE" -> remove(command[1]);
        case "RENAME" -> rename(command[1]);
        case "EMPTY" -> {}
        default->
            System.out.println("Unknown command: " + command[0]);
      }
    } else {
      throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
  }
}
