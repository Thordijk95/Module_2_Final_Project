package com.nedap.university.util.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATASIZE;

import com.nedap.university.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Packets.AckPacket;
import com.nedap.university.util.Packets.InboundPacket;
import com.nedap.university.util.Packets.InterfacePacket;
import com.nedap.university.util.Packets.OutboundPacket;
import java.awt.print.PrinterGraphics;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientCommandHandler extends abstractCommandHandler{

  InetAddress address;
  int port;

  public ClientCommandHandler(DatagramSocket socket, InetAddress address, int port){
    super(socket);
    this.address = address;
    this.port = port;
  }

  @Override
  public void getList(InetAddress hostname, int port) throws IOException {
    System.out.println("Retrieving list from server");
    // sent the request to get the list
    InterfacePacket packet = new OutboundPacket(Requests.LIST, true, false, 0, "", new byte[1]);
    DatagramPacket outboundDatagramPacket = new DatagramPacket(packet.getData(), packet.getData().length, address, port);
    socket.send(outboundDatagramPacket);

    byte[] buffer = new byte[DatagramProperties.DATAGRAMSIZE];
    DatagramPacket ackDatagramPacket = new DatagramPacket(buffer, buffer.length);
    socket.receive(ackDatagramPacket);
    InterfacePacket ackPacket = new InboundPacket(ackDatagramPacket.getData());
    if (ackPacket.isAcknowledgement()) {
      System.out.println("Received acknowledgement, waiting for list!");
      while(true) {
        DatagramPacket inboundDatagramPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(inboundDatagramPacket);
        InterfacePacket inboundPacket = new InboundPacket(inboundDatagramPacket.getData());
        System.out.println("Received a packet after acknowledgement");
        if (inboundPacket.getRequestType() == Requests.LIST) {
          // The request for the list has been acknowledged, the list of files is contained in data
          String[] fileList = Conversions.fromByteArrayToStringArray(inboundPacket.getData(),
              DatagramProperties.SEPERATOR.toString());
          for (String file : fileList) {
            if (file.isEmpty()) {
              System.out.println("File list is empty!");
              // end  of list has been reached stop waiting for more lists
              return;
            }
            System.out.println(file);
          }
        }
      }
    } else {
      System.out.println("Request was not acknowledged");
    }
  }

  @Override
  public void upload(String filePath, byte[] ignored_data) throws IOException {
    // TODO remove static file path
    filePath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf";
    System.out.println("Uploading file: " + filePath + " to server");
    byte[] data = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    ArrayList<byte[]> dataList = util.splitData(data);
    int packetCount = dataList.size();
    int packetCounter = 0;
    boolean first = true;
    String filename = filePath.split("/")[filePath.split("/").length - 1];
    for (byte[] dataPacket : dataList) {
      InterfacePacket newPacket =
          new OutboundPacket(Requests.UPLOAD, first, false, packetCounter, filename, dataPacket);
      DatagramPacket newDatagramPacket
          = new DatagramPacket(newPacket.getData(), newPacket.getData().length, address, port);
      System.out.println("Sending packet " + (packetCounter+1) + ": " + packetCount);
      socket.send(newDatagramPacket);
      packetCounter++;
      first = false;

      byte[] buffer = new byte[DatagramProperties.DATAGRAMSIZE];
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      socket.receive(response);
      InterfacePacket inboundPacket = new InboundPacket(response.getData());
      if (inboundPacket.isAcknowledgement() && inboundPacket.getSequenceNumber()==newPacket.getSequenceNumber()) {
        System.out.println("Succesfully acknowledged a packet");
      } else {
        System.out.println("fail!");
        System.out.println("acknowledgement: " + inboundPacket.isAcknowledgement());
        System.out.println("Last packet send sequence number: " + newPacket.getSequenceNumber());
        System.out.println("Last packet received sequence number: " + inboundPacket.getSequenceNumber());
      }
      if (packetCounter == Math.pow(2,8)) { // Sequence number field is 8 bit, after number 2^8 wrap back to 0
        packetCounter = 0;
      }
    }
  }
  @Override
  public void download(String filePath, InetAddress ignoredHostname, int ignoredPort) throws IOException {
    System.out.println("Downloading file: " + filePath + " from server");
    InterfacePacket outBoundPacket = new OutboundPacket(Requests.DOWNLOAD, false, false, 0, "", new byte[1]);
    DatagramPacket datagramPacket =
        new DatagramPacket(outBoundPacket.getData(), outBoundPacket.getData().length, address, port);
    socket.send(datagramPacket);
    // wait for an acknowledgement
    DatagramPacket ackDatagramPacket = new DatagramPacket(new byte[1024], 1024);
    socket.receive(ackDatagramPacket);
    InterfacePacket inboundPacket = new InboundPacket(ackDatagramPacket.getData());
    if (inboundPacket.isAcknowledgement() && inboundPacket.getRequestType() == Requests.DOWNLOAD) {
      while(true) {
        // Start receiving the file
        DatagramPacket downloadDatagramPacket = new DatagramPacket(new byte[DATASIZE], DATASIZE);
        socket.receive(downloadDatagramPacket);
        InterfacePacket downloadPacket = new InboundPacket(downloadDatagramPacket.getData());
        util.safeFile(filePath, downloadPacket.getData());
      }
    }
  }

  @Override
  public void remove(String filePath) throws IOException {
    System.out.println("Removing file: " + filePath + " from server");
  }

  @Override
  public void rename(String filePath) throws IOException {
    System.out.println("Renaming file: " + filePath + " on server");
  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException {
    DatagramPacket packet = new DatagramPacket(new byte[1], 1, hostname, port);
    System.out.println("Sending data gram");
    super.socket.send(packet);
    Thread.sleep(500);
    byte[] response = new byte[512];
    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
    socket.receive(responsePacket);
    return responsePacket.getLength() > 0;
  }

}
