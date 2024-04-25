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

  public static byte[] loadFile(String filepath) throws IOException {
    Path path = Paths.get(filepath);
    byte[] data = Files.readAllBytes(path);
    System.out.println(data.length + " bytes loaded from file");
    return data;
  }

  public void safeFile(String filePath, byte[] data) throws IOException {
    Path path;
    try {
      path = Paths.get(filePath);
    } catch (InvalidPathException e){
      path = Paths.get(System.getProperty("user.home"), "logs", filePath);
    }
    if (Files.exists(path)) {
     removeFile(filePath);
    }
    Files.createFile(path);
    FileOutputStream outputStream = new FileOutputStream(filePath);
    outputStream.write(data);
    outputStream.close();
  }

  public void removeFile(String filePath) throws IOException {
    Path path;
    try {
      path = Paths.get(filePath);
    } catch (InvalidPathException e) {
      throw new IOException("failed to remove the file: " + filePath);
    }
    if (Files.exists(path)) {
      Files.delete(path);
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

  public static boolean isValidCommand(String command) {
    String[] commandParts = command.split(" ");
    if (commandParts.length != 2) {
      return false;
    }
    if (Requests.validRequest(commandParts[0])) {
      return validFileName(commandParts[1]);
    }
    return false;
  }

  public static boolean validFileName(String fileName) {
    try {
      String[] fileParts = fileName.split("\\.");
      return !(fileParts.length != 2 || fileParts[0].isEmpty() || fileParts[1].isEmpty());
    } catch (Exception e) {
      System.out.println("Provided filename syntax is incorrect: <filename>.<filetype> required");
      return false;
    }
  }
}
