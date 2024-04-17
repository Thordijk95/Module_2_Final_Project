package com.nedap.university.util.CommandHandler;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerCommandHandler extends abstractCommandHandler{

  String storageDirectory;
  public ServerCommandHandler(DatagramSocket socket, String storageDirectory) {
    super(socket);
    this.storageDirectory = storageDirectory;
  }

  @Override
  public void getList() {

  }

  @Override
  public void upload(String filePath, InetAddress hostname, int port, byte[] data) throws IOException {
    System.out.println("Saving inbound data on the server at location " + storageDirectory + filePath);
    util.safeFile(storageDirectory + filePath, data);
  }

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
