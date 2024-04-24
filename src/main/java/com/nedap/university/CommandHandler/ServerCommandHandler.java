package com.nedap.university.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.Packets.ErrorPacket;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public class ServerCommandHandler extends abstractCommandHandler{

  public ServerCommandHandler(DatagramSocket socket, String storageDirectory) {
    super(socket, storageDirectory);
  }

  @Override
  public void getList(InetAddress address, int port, InterfacePacket requestPacket) throws IOException {
    // Acknowledge the request packet
    acknowledge(requestPacket, address, port);
    ArrayList<String> fileList = util.getFileList(storageDirectory);
    System.out.println(fileList);
    byte[] data = Conversions.fromFileListToByteArray(fileList);
    ArrayList<byte[]> dataList = util.splitData(data);
    slidingWindow.sender(socket, address, port, Requests.LIST, dataList, "");
  }

  // A file is uploaded to the serevr
  @Override
  public void upload(String filePath, InetAddress address, int port, InterfacePacket requestPacket) throws IOException {
    // Acknowledge the request packet
    acknowledge(requestPacket, address, port);
    // Start receiving files to upload
    byte[] data = receivingWindow.receiver(socket, address, port, Requests.UPLOAD);
    util.safeFile(storageDirectory + "/" + filePath, data);
  }

  // A file is downloaded from the server
  @Override
  public void download(String fileName, InetAddress address, int port, InterfacePacket requestPacket) throws IOException{
    System.out.println("Loading file: " + fileName);
    try {
      // Load the file
      byte[] data = Util.loadFile(storageDirectory + "/" + fileName);
      ArrayList<byte[]> dataList = util.splitData(data);
      // Acknowledge the request packet
      acknowledge(requestPacket, address, port);
      // Use the sliding window to send the data
      slidingWindow.sender(socket, address, port, Requests.DOWNLOAD, dataList, fileName );
    } catch (IOException e) {
      e.printStackTrace();
      InterfacePacket errorPacket = new ErrorPacket("IOException");
      slidingWindow.sendPacket(socket, address, port, errorPacket);
    }
  }

  @Override
  public void remove(String filePath, InetAddress address, int port, InterfacePacket requestPacket) throws IOException{
    try {
      util.removeFile(storageDirectory + "/" + filePath);
      // Acknowledge the request packet after succesfully performing the command
      acknowledge(requestPacket, address, port);
    } catch (IOException e) {
      e.printStackTrace();
      InterfacePacket errorPacket = new ErrorPacket("IOException");
      slidingWindow.sendPacket(socket, address, port, errorPacket);
      e.printStackTrace();
    }
  }

  @Override
  public void rename(String filePath, String newFileName, InetAddress address, int port,  InterfacePacket requestPacket) throws IOException{
    byte[] data = Util.loadFile(storageDirectory + "/" + filePath);
    util.removeFile(storageDirectory + "/" + filePath);
    util.safeFile(storageDirectory + "/" + newFileName, data);
    // Acknowledge the request packet
    acknowledge(requestPacket, address, port);
  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port)
      throws IOException, InterruptedException {
    return false;
  }
}
