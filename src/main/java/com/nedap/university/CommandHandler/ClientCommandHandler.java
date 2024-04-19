package com.nedap.university.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.ConnectionPacket;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public class ClientCommandHandler extends abstractCommandHandler{

  InetAddress address;
  int port;


  public ClientCommandHandler(DatagramSocket socket, InetAddress address, int port, String storageDirectory){
    super(socket, storageDirectory);
    this.address = address;
    this.port = port;
  }

  @Override
  public void getList(InetAddress ignored_address, int ignored_port) throws IOException {
    System.out.println("Retrieving list from server");
    // sent the request to get the list
    InterfacePacket outboundPacket = new OutboundPacket(address, port, Requests.LIST, true, false, 0, "", new byte[1]);
    DatagramPacket outboundDatagramPacket = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, address, port);
    socket.send(outboundDatagramPacket);

    DatagramPacket ackDatagramPacket = new DatagramPacket(new byte[DatagramProperties.DATAGRAMSIZE], DatagramProperties.DATAGRAMSIZE);
    socket.receive(ackDatagramPacket);
    InterfacePacket ackPacket = new InboundPacket(ackDatagramPacket);
    if (ackPacket.isAcknowledgement()) {
      System.out.println("Received acknowledgement for list request, waiting for list!");
      slidingWindow.addAcknowledgedPacket(ackPacket);
      receivingWindow.receiver(socket, Requests.LIST);
    } else {
      System.out.println("Request was not acknowledged");
    }
  }

  @Override
  public void upload(String fileName, byte[] ignored_data) throws IOException {
    fileName = "tiny.pdf";
    System.out.println("Uploading file: " + fileName + " to server");
    byte[] data = util.loadFile(storageDirectory + "/" + fileName);
    ArrayList<byte[]> dataList = util.splitData(data);
    // start the sending segment
    slidingWindow.sender(socket, address, port, Requests.UPLOAD, timeout,  dataList, fileName);
    System.out.println("upload finished!");
  }

  @Override
  public void download(String fileName, InetAddress ignoredHostname, int ignoredPort) throws IOException {
    System.out.println("Downloading file: " + fileName + " from server");
    InterfacePacket outBoundPacket =
        new OutboundPacket(address, port, Requests.DOWNLOAD, false, false, 0, fileName, new byte[1]);
    DatagramPacket datagramPacket =
        new DatagramPacket(outBoundPacket.getData(), outBoundPacket.getData().length, address, port);
    socket.send(datagramPacket);
    timeout.createTimer(outBoundPacket, new Timer(), socket);
    while(true) {
      // wait for an acknowledgement
      DatagramPacket ackDatagramPacket = new DatagramPacket(new byte[1024], 1024);
      socket.receive(ackDatagramPacket);
      InterfacePacket ackPacket = new InboundPacket(ackDatagramPacket);
      if (ackPacket.isAcknowledgement() && ackPacket.getRequestType() == Requests.DOWNLOAD) {
          slidingWindow.addAcknowledgedPacket(ackPacket);
          while(true) {
            // Start receiving the file
            DatagramPacket downloadDatagramPacket = new DatagramPacket(new byte[DATA_SIZE],
                DATA_SIZE);
            socket.receive(downloadDatagramPacket);
            InterfacePacket downloadPacket = new InboundPacket(downloadDatagramPacket);
            acknowledge(downloadPacket.getRequestType(), downloadPacket.getSequenceNumber(), downloadDatagramPacket.getAddress(), downloadDatagramPacket.getPort());
            if (downloadPacket.isFirstPacket() && !(downloadPacket.getFileName().isEmpty() || downloadPacket.getFileType().isEmpty())) {
              util.removeFile(storageDirectory + downloadPacket.getFileName()+"."+downloadPacket.getFileType());
            }
            util.safeFile(storageDirectory + downloadPacket.getFileName()+"."+downloadPacket.getFileType(), downloadPacket.getData());
          }
      }
    }
  }

  @Override
  public void remove(String fileName) throws IOException {
    System.out.println("Removing file: " + fileName + " from server");
  }

  @Override
  public void rename(String fileName) throws IOException {
    System.out.println("Renaming file: " + fileName + " on server");
  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException {
    InterfacePacket packet = new ConnectionPacket();
    DatagramPacket datagramPacket = new DatagramPacket(packet.getData(), packet.getData().length-1, hostname, port);
    System.out.println("Sending data gram");
    slidingWindow.send(socket, hostname, port, Requests.CONNECT, timeout, packet.isFirstPacket(),
        packet.isAcknowledgement(), packet.getSequenceNumber(), packet.getFileName(), packet.getData());
    byte[] response = new byte[512];
    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
    socket.receive(responsePacket);
    InterfacePacket inboundPacket = new InboundPacket(responsePacket);
    slidingWindow.verifyAcknowledgement(inboundPacket);
    return responsePacket.getLength() > 0;
  }

}
