package com.nedap.university.util.CommandHandler;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerCommandHandler extends abstractCommandHandler{

  public ServerCommandHandler(DatagramSocket socket) {
    super(socket);
  }

  @Override
  public void getList() {

  }

  @Override
  public void upload(String filePath, String hostname, int port) throws IOException {

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
