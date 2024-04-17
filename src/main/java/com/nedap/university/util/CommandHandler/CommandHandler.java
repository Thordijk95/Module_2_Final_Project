package com.nedap.university.util.CommandHandler;

import com.nedap.university.exceptions.IncorrectArgumentException;
import java.io.IOException;
import java.net.InetAddress;

public interface CommandHandler {

  void executeCommand(String[] command, InetAddress hostname, int port, byte[] data) throws IncorrectArgumentException, IOException;

  void getList() throws IOException;

  void upload(String filePath, byte[] data) throws IOException;

  void download(String filePath);

  void remove(String filePath);

  void rename(String filePath);

  boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException;
}
