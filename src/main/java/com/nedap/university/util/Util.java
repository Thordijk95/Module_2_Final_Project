package com.nedap.university.util;

import com.nedap.university.Communication.Requests;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {

  public Util(){}

  public ArrayList<byte[]> splitData(byte[] data) {

    ArrayList<byte[]> dataList = new ArrayList<>();
    int dataPointer = 0;
    while (dataPointer < data.length) {
      byte[] tmpData = new byte[Math.min(data.length-dataPointer, DatagramProperties.DATA_SIZE)];
      System.arraycopy(data, dataPointer, tmpData, 0, Math.min(data.length-dataPointer, DatagramProperties.DATA_SIZE));
      dataList.add(tmpData);
      dataPointer += Math.min(data.length-dataPointer, DatagramProperties.DATA_SIZE);
    }
    return dataList;
  }

  public byte[] loadFile(String filepath) throws IOException {
    Path path = Paths.get(filepath);
    byte[] data = Files.readAllBytes(path);
    System.out.println(data.length + " bytes loaded from file");
    return data;
  }

  public void safeFile(String filePath, byte[] data) throws IOException {
    String userDirectory = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    System.out.println("User directory: " + userDirectory);

    System.out.println(filePath);
    Path path;
    try {
      path = Paths.get(filePath);
    } catch (InvalidPathException e){
      path = Paths.get(System.getProperty("user.home"), "logs", filePath);
    }
    if (Files.exists(path)) {
     FileOutputStream outputStream = new FileOutputStream(filePath, true);
     outputStream.write(data);
     outputStream.close();
    } else {
     Files.createFile(path);
     FileOutputStream outputStream = new FileOutputStream(filePath);
     outputStream.write(data);
     outputStream.close();
    }
  }

  public void removeFile(String filePath) throws IOException {
    Path path;
    try {
      path = Paths.get(filePath);
    } catch (InvalidPathException e) {
      System.out.println("failed to remove the file: " + filePath);
      return;
    }
    if (Files.exists(path)) {
      Files.delete(path);
      System.out.println("removed file");
    } else {
      System.out.println("failed to remove the file: " + path);
    }
  }

  public ArrayList<String> getFileList(String directoryPath) throws IOException {
    // Get the list of files from the storage directory
    ArrayList<String> fileList = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
      for (Path path : directoryStream) {
        if(!Files.isDirectory(path)) {
          fileList.add(path.getFileName().toString());
        }
      }
    }
    return fileList;
  }

  public static boolean fileNameRequired(Requests requestsType) {
    return switch (requestsType) {
      case DOWNLOAD, UPLOAD, REMOVE, RENAME -> true;
      default -> false;
    };
  }

  public byte[] lastPacketInList(byte[] packet, ArrayList<byte[]> packetList ) {
    if (Arrays.equals(packetList.get(packetList.size() - 1), packet)) {
      byte[] newPacket = new byte[packet.length+1];
      byte[] closingByte = new byte[] {0x03};
      System.arraycopy(packet, 0, newPacket, 0, packet.length);
      System.arraycopy(closingByte, 0, newPacket, packet.length, 1);
      packet = newPacket;
    }
    return packet;
  }
}
