package com.nedap.university.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Communication.ReceiveWindow;
import com.nedap.university.Communication.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
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
  public void getList(InetAddress address, int port, InterfacePacket empty_packet) throws IOException {
    System.out.println("Retrieving list from server");
    // send the request to get the list
    InterfacePacket outboundPacket = new OutboundPacket(address, port, Requests.LIST, true,  false, false, 0, "", new byte[1]);
    DatagramPacket outboundDatagramPacket = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, address, port);
    socket.send(outboundDatagramPacket);

    // Wait for an acknowledgement packet for the request
    InterfacePacket ackPacket = receivingWindow.receive(socket);
    if (ackPacket.isAcknowledgement()) {
      System.out.println("Received acknowledgement for list request, waiting for list!");
      receivingWindow.addAcknowledgedPacket(ackPacket);
      // Start receiving the list data
      byte[] data = receivingWindow.receiver(socket, address, port, Requests.LIST);

      String dataString = Conversions.fromByteArrayToString(data, data.length, 0);
      System.out.println(dataString);

    } else {
      System.out.println("Request was not acknowledged");
    }
  }

  @Override
  public int upload(String fileName, InetAddress address, int port, InterfacePacket empty_packet) throws IOException {
    // Send upload request without data to tell server what is going to happen
    InterfacePacket uploadRequestPacket = new OutboundPacket(address, port, Requests.UPLOAD, false,  false, false, 255, fileName, new byte[0]);
    slidingWindow.sendPacket(socket, address, port, uploadRequestPacket);

    while(!(slidingWindow.verifyAcknowledgement(slidingWindow.receive(socket)))) {
      // Wait for the acknowledgement of the request
    }
    System.out.println("Uploading file: " + fileName + " to server");
    byte[] data = Util.loadFile(storageDirectory + "/" + fileName);
    ArrayList<byte[]> dataList = util.splitData(data);
    // start the sending segment
    slidingWindow.sender(socket, address, port, Requests.UPLOAD, dataList, fileName);
    System.out.println("upload finished!");
    return data.length;
  }

  @Override
  public int download(String fileName, InetAddress address, int port, InterfacePacket empty_packet) throws IOException {
    System.out.println("Downloading file: " + fileName + " from server");
    // send the download request
    InterfacePacket downloadRequestPacket =
        new OutboundPacket(address, port, Requests.DOWNLOAD, false, false, false, 255, fileName, new byte[1]);
    slidingWindow.sendPacket(socket, address, port, downloadRequestPacket);
    System.out.println(downloadRequestPacket.getAddress());
    // wait for an acknowledgement of the request
    while(!(slidingWindow.verifyAcknowledgement(slidingWindow.receive(socket)))) {
      // Wait for the acknowledgement of the request
    }
    // start the receiver to receive the incoming file
    byte[] data = receivingWindow.receiver(socket, address, port, Requests.DOWNLOAD);
    util.removeFile(storageDirectory+"/"+fileName);
    util.safeFile(storageDirectory+"/"+fileName, data);
    return data.length;
  }

  @Override
  public void remove(String fileName, InetAddress address, int port, InterfacePacket empty_packet) throws IOException {
    System.out.println("Removing file: " + fileName + " from server");
    InterfacePacket removeRequestPacket = new OutboundPacket(address, port, Requests.REMOVE, false, false, false, 255, fileName, new byte[0]);
    slidingWindow.sendPacket(socket, address, port, removeRequestPacket);
    while(!(slidingWindow.verifyAcknowledgement(slidingWindow.receive(socket)))) {
      // Wait for the acknowledgement of the request
    }
  }

  @Override
  public void rename(String fileName, String newFileName, InetAddress address, int port, InterfacePacket empty_packet) throws IOException {
    System.out.println("Renaming file: " + fileName + " on server to " + newFileName);
    byte[] newFileNameBytes = newFileName.getBytes();
    InterfacePacket renamePacket = new OutboundPacket(address, port, Requests.RENAME, true, false, false, 0, fileName, newFileNameBytes);
    slidingWindow.sendPacket(socket, address, port, renamePacket);
    while(!(slidingWindow.verifyAcknowledgement(slidingWindow.receive(socket)))) {
      // Wait for the acknowledgement of the request
    }
    System.out.println("Rename request acknowledged!");
  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress address, int port) throws IOException, InterruptedException {
    InterfacePacket packet = new ConnectionPacket(address, port);
    DatagramPacket datagramPacket = new DatagramPacket(packet.getData(), packet.getData().length-1, address, port);
    System.out.println("Sending connection request");
    slidingWindow.sendPacket(socket, address, port, packet);
    while(!(slidingWindow.verifyAcknowledgement(slidingWindow.receive(socket)))) {
      // Wait for the acknowledgement of the request
    }
    return true;
  }

}
