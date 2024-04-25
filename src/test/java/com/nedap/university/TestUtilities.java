package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.Communication.Requests;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import com.nedap.university.Packets.OutboundPacket;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUtilities {
  Util util;

  String directoryPath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files";
  String savedDirectoryPath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved";
  String fileName = "tiny.pdf";

  InetAddress address = InetAddress.getLoopbackAddress();
  int port = 8080;
  Requests request = Requests.EMPTY;


  @BeforeEach
  public void setUp() {
    util = new Util();
  }

  @Test
  public void testSplitData() throws IOException {
    byte[] data = Util.loadFile(directoryPath + "/" + fileName);
    int expectedLength = data.length / DatagramProperties.DATA_SIZE
        + (data.length % DatagramProperties.DATA_SIZE > 0 ? 1 : 0);
    ArrayList<byte[]> dataList = util.splitData(data);
    assertEquals(expectedLength, dataList.size());
  }


  @Test
  public void testSafeOverWriteFile() throws IOException {
    util.removeFile(savedDirectoryPath + "/" + fileName);
    byte[] data = Util.loadFile(directoryPath + "/" + fileName);
    util.safeFile(savedDirectoryPath + "/" + fileName, data);
    byte[] data2 = Util.loadFile(savedDirectoryPath + "/" + fileName);
    assertArrayEquals(data, data2);
  }

  @Test
  public void testGetFileList() throws IOException {
    ArrayList<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("tiny.pdf");
    expectedFiles.add("medium.pdf");
    expectedFiles.add("large.pdf");

    ArrayList<String> files = util.getFileList(directoryPath);

    expectedFiles.sort(String.CASE_INSENSITIVE_ORDER);
    files.sort(String.CASE_INSENSITIVE_ORDER);

    assertEquals(expectedFiles, files);
  }

  @Test
  public void testCRC32Checksum() throws IOException {
    byte[] data = Util.loadFile(savedDirectoryPath+"/"+fileName);
    ArrayList<byte[]> dataList = util.splitData(data);
    for (byte[] packet : dataList) {
      InterfacePacket outboundPacket = new OutboundPacket(address, port, request, false, false, false, 0, "tiny.pdf", packet);
      DatagramPacket outboundDatagramPacket = new DatagramPacket(outboundPacket.getData(), outboundPacket.getData().length, address, port);
      InterfacePacket inboundPacket = new InboundPacket(outboundDatagramPacket);
      assertEquals(outboundPacket.getCRC32(), inboundPacket.getCRC32());
      assertEquals(inboundPacket.getCRC32(), inboundPacket.calculateCRC(inboundPacket.getData()));
    }
  }
}

