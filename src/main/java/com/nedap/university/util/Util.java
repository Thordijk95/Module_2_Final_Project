package com.nedap.university.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Util {

  public Util(){}

  public ArrayList<byte[]> splitData(byte[] data, int dataSize) {

    ArrayList<byte[]> dataList = new ArrayList<>();
    int dataPointer = 0;
    while (dataPointer < data.length) {
      byte[] tmpData = new byte[Math.min(data.length-dataPointer, dataSize)];
      System.arraycopy(data, dataPointer, tmpData, 0, Math.min(data.length-dataPointer, dataSize));
      dataList.add(tmpData);
      dataPointer += Math.min(data.length-dataPointer, dataSize);
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
     Path path = Paths.get(filePath);
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
}
