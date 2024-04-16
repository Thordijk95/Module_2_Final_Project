package com.nedap.university.util.CommandHandler;

import com.nedap.university.exceptions.IncorrectArgumentException;
import java.io.IOException;
import java.net.InetAddress;

public interface CommandHandler {

  void executeCommand(String[] command, String hostname, int port) throws IncorrectArgumentException, IOException;

  void getList();

  void upload(String filePath, String hostname, int port) throws IOException;

  void download(String filePath);

  void remove(String filePath);

  void rename(String filePath);

  boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException;
}
