package com.nedap.university.CommandHandler;

import com.nedap.university.Communication.Requests;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.exceptions.IncorrectArgumentException;
import java.io.IOException;
import java.net.InetAddress;

public interface CommandHandler {

  void executeCommand(String[] command, InetAddress hostname, int port, byte[] data) throws IncorrectArgumentException, IOException;

  void getList(InetAddress hostname, int port) throws IOException;

  void upload(String filePath, InetAddress address, int port) throws IOException;

  byte[]  download(String fileName, InetAddress hostname, int port) throws IOException;

  void remove(String fileName) throws IOException;

  void rename(String filePath, String newFileName) throws IOException;

  void acknowledge(InterfacePacket packet, InetAddress hostname, int port) throws IOException;

  boolean testConnectionAtRunTime(InetAddress hostname, int port) throws IOException, InterruptedException;
}
