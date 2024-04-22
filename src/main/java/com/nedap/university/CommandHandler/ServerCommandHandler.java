package com.nedap.university.CommandHandler;

import static com.nedap.university.util.DatagramProperties.DATA_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.util.Conversions;
import com.nedap.university.Packets.ErrorPacket;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
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
  public void getList(InetAddress address, int port) throws IOException {
    System.out.println(storageDirectory);
    ArrayList<String> fileList = util.getFileList(storageDirectory);
    System.out.println(fileList);
    byte[] data = Conversions.fromFileListToByteArray(fileList);
    ArrayList<byte[]> dataList = util.splitData(data);
    boolean firstPacket = true;
    int sequenceNumber = 0;
    slidingWindow.sender(socket, address, port, Requests.LIST, dataList, "");
  }

  // A file is uploaded to the serevr
  @Override
  public void upload(String filePath, byte[] data) throws IOException {
    System.out.println("Saving the file");
    util.safeFile(storageDirectory + filePath, data);
  }

  // A file is downloaded from the server
  @Override
  public byte[] download(String fileName, InetAddress address, int port) throws IOException{
    System.out.println("Loading file: " + fileName);
    try {
      // Load the file
      byte[] data = util.loadFile(storageDirectory + fileName);
      ArrayList<byte[]> dataList = util.splitData(data);
      // Use the sliding window to send the data
      slidingWindow.sender(socket, address, port, Requests.DOWNLOAD, dataList, fileName );
    } catch (IOException e) {
      System.out.println(e.getMessage());
      InterfacePacket errorPacket = new ErrorPacket();
    }
     return null;
  }

  @Override
  public void remove(String filePath) throws IOException{

  }

  @Override
  public void rename(String filePath) throws IOException{

  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port)
      throws IOException, InterruptedException {
    return false;
  }
}
