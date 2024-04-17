package com.nedap.university.util.CommandHandler;

import com.nedap.university.util.Packet;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ServerCommandHandler extends abstractCommandHandler{

  String storageDirectory;
  public ServerCommandHandler(DatagramSocket socket, String storageDirectory) {
    super(socket);
    this.storageDirectory = storageDirectory;
  }

  @Override
  public void getList() throws IOException {
    ArrayList<String> fileList = util.getFileList(storageDirectory);
    Packet outboundPacket = new Packet();
  }

  // A file is uploaded to the serevr
  @Override
  public void upload(String filePath, byte[] data) throws IOException {
    System.out.println("Saving inbound data on the server at location " + storageDirectory + filePath);
    util.safeFile(storageDirectory + filePath, data);
  }

  // A file is downloaded from the server
  @Override
  public void download(String filePath) {

  }

  @Override
  public void remove(String filePath) {

  }

  @Override
  public void rename(String filePath) {

  }

  @Override
  public boolean testConnectionAtRunTime(InetAddress hostname, int port)
      throws IOException, InterruptedException {
    return false;
  }
}
