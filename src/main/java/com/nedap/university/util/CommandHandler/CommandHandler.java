package com.nedap.university.util.CommandHandler;

import com.nedap.university.Requests;
import com.nedap.university.exceptions.IncorrectArgumentException;
import java.io.IOException;
import java.net.InetAddress;

public interface CommandHandler {

  void executeCommand(String[] command, InetAddress hostname, int port, byte[] data) throws IncorrectArgumentException, IOException;

  void getList(InetAddress hostname, int port) throws IOException;

  void upload(String filePath, byte[] data) throws IOException;

  void download(String filePath, InetAddress hostname, int port) throws IOException;

  void remove(String filePath) throws IOException;

  void rename(String filePath) throws IOException;

  void acknowledge(Requests request, int sequenceNumber, InetAddress hostname, int port) throws IOException;

  boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException;
}
