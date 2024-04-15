package com.nedap.university.client;

import com.nedap.university.Exceptions.IncorrectArgumentException;
import com.nedap.university.Requests;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MyClientTUI {
  static final int maxTries = 3;

  public static void main(String[] arg) {
    MyClientTUI myClientTUI = new MyClientTUI();
    myClientTUI.runTUI();
  }

  public void runTUI() {
    Scanner input = new Scanner(System.in);
    MyClient myClient;
    for (int i = 0; i < maxTries; i++) {
      try {
        System.out.println(
            "Please provide the address/hostname where to connect to the file server:");
        String hostName = input.nextLine();
        String port = input.nextLine();
        myClient = new MyClient(new String[] {hostName, port});
        help();
        // Start taking in commands from the user and passing them on to the server
        while(true) {
          String command = input.nextLine();
          String[] args = command.split(" ");
          if (args[0].equals(Requests.EXIT.name().toLowerCase())) {
            System.out.println("Stopping the client!");
            break;
          } else if (!Requests.validRequest(args[0])) {
            myClient.executeCommand(args);
          } else {
            System.out.println("Invalid request type");
            help();
          }

        }
        break;
      } catch (UnknownHostException e) {
        System.out.println("Unknown host: " + e.getMessage());
      } catch (SocketException e) {
        System.out.println("Unable to establish a socket");
      } catch (IncorrectArgumentException e) {
        System.out.println(e.getMessage());
      }
      System.out.println(maxTries-i-1 + " tries remaining");
    }


  }


  public static void help() {
    StringBuilder builder = new StringBuilder();
    builder.append("This the help for the textual interface of the file server client.\n");
    builder.append("The following commands are available:\n");
    builder.append("LIST - List all files currently on the server\n");
    builder.append("UPLOAD <filepath> - Upload a file to the server\n");
    builder.append("DOWNLOAD <filepath> - Download a file from the server\n");
    builder.append("REMOVE <filepath> - Download a file from the server\n");
    builder.append("RENAME <filepath> - Rename a file from the server\n");
    builder.append("EXIT - to Exit the server\n");
    System.out.println(builder);
  }
}
