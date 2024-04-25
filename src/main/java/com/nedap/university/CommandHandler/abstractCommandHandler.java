package com.nedap.university.CommandHandler;

import static java.lang.reflect.Array.get;

import com.nedap.university.Communication.ReceiveWindow;
import com.nedap.university.Communication.Requests;
import com.nedap.university.Communication.Window;
import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.Packets.AckPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Communication.SlidingWindow;
import com.nedap.university.util.Timeout;
import com.nedap.university.util.Util;
import com.nedap.university.util.Conversions;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Timer;

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
  public void executeCommand(String[] command, InetAddress address, int port, byte[] data, InterfacePacket packet) throws IncorrectArgumentException, IOException {
    LocalDateTime startTime = LocalDateTime.now();
    String newFileName = "";
    int bytes = 0;
    if (!(command[0].toUpperCase().equals(Requests.LIST.toString().toUpperCase())) && command.length == 2) {
      if (!Util.validFileName(command[1])) {
        throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
            + "Provided arguments are: " + Arrays.toString(command));
      }
    }
    if (command[0].toUpperCase().equals(Requests.RENAME.toString().toUpperCase()) && command.length == 3) {
      newFileName = command[2];
      // Client side
      if(!Util.validFileName(command[1]) || !Util.validFileName(command[2])) {
        throw new IncorrectArgumentException("Incorrect arguments: RENAME <filename> <filename>\n "
            + "Provided arguments are: " + Arrays.toString(command));
      }

    } else if (command[0].toUpperCase().equals(Requests.RENAME.toString().toUpperCase()) && command.length == 2) {
      // Server side
      newFileName = Conversions.fromByteArrayToString(packet.getData(), packet.getData().length, 0);
      if(!Util.validFileName(command[1]) || !Util.validFileName(newFileName)) {
          throw new IncorrectArgumentException("Incorrect arguments: RENAME <filename> <filename>\n "
              + "Provided arguments are: " + Arrays.toString(command));
      }

    } else if (command[0].equals(Requests.RENAME.toString()) && command.length < 3){
      throw new IncorrectArgumentException("Incorrect arguments: RENAME <filename> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
    if (command[0].toUpperCase().equals(Requests.LIST.name()) || command.length > 1) {
      switch (command[0].toUpperCase()) {
        case "LIST" -> getList(address, port, packet);
        case "UPLOAD" ->  bytes = upload(command[1].toLowerCase(), address, port, packet);
        case "DOWNLOAD" -> bytes = download(command[1].toLowerCase(), address, port, packet);
        case "REMOVE" -> remove(command[1].toLowerCase(), address, port, packet);
        case "RENAME" -> rename(command[1].toLowerCase(), newFileName.toLowerCase(), address, port, packet);
        case "EMPTY" -> {}
        default->
            System.out.println("Unknown command: " + command[0]);
      }
    } else {
      throw new IncorrectArgumentException("Incorrect arguments: <Command> <filename>\n "
          + "Provided arguments are: " + Arrays.toString(command));
    }
    LocalDateTime endTime = LocalDateTime.now();
    long milliSeconds = Duration.between(startTime, endTime).toMillis();
    System.out.println("Time required to execute command: " + command[0] + " was " + milliSeconds +" milliseconds!");
    System.out.println("Total bytes : " + bytes);
    if (milliSeconds >= 1000) {
      System.out.println("Speed: " + (bytes/(milliSeconds/1000)) +" Bytes/second");
    } else {
      System.out.println("Speed: " + (bytes/(milliSeconds)) +" Bytes/milliSecond");
    }
  }

  public void acknowledge(InterfacePacket packet, InetAddress address, int port) throws IOException {
    slidingWindow.acknowledgePacket(socket, address, port, packet);
  };
}
