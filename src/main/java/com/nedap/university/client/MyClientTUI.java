package com.nedap.university.client;

import com.nedap.university.exceptions.IncorrectArgumentException;
import com.nedap.university.Communication.Requests;
import java.io.IOException;
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
            "Please provide the address/hostname where to connect to the file server: (default = 172.16.1.1)");
        String hostname = input.nextLine();
        hostname = "172.16.1.1";
        System.out.println("Provide the well known port of the server: (default = 8080)");
        String port = input.nextLine();
        port = "8080";
        myClient = new MyClient(new String[] {hostname, port});
        help();
        // Start taking in commands from the user and passing them on to the server
        while(true) {
          System.out.println("Provide command line arguments:");
          String command = input.nextLine();
          String[] args = command.split(" ");
          if (args[0].toUpperCase().equals(Requests.EXIT.name())) {
            System.out.println("Stopping the client!");
            break;
          } else if (args[0].toUpperCase().equals(Requests.HELP.name())) {
            help();
          } else if (Requests.validRequest(args[0].toUpperCase())) {
            try {
              myClient.clientCommandHandler.executeCommand(args, InetAddress.getLocalHost(), Integer.parseInt(port), null, null);
            } catch (IncorrectArgumentException e) {
              System.out.println(e.getMessage());
            } catch (IOException e) {
              System.out.println("The provided filepath is incorrect");
              e.printStackTrace();
            }
          } else {
            System.out.println("Invalid request");
            help();
          }
        }
        break;
      } catch (UnknownHostException e) {
        System.out.println("Unknown host: " + e.getMessage());
      } catch (SocketException e) {
        System.out.println("Unable to establish a socket");
      } catch (IncorrectArgumentException e) {
        System.out.println("Incorrect arguments: " + e.getMessage());
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
    builder.append("RENAME <current filepath> <new filepath> - Rename a file from the server\n");
    builder.append("EXIT - to Exit the server\n");
    System.out.println(builder);
  }
}
