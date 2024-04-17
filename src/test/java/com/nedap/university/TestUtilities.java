package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUtilities {
  Util util;

  String filepath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf";

  int DATASIZE = 65535 - 10;
  @BeforeEach
  public void setUp() {
    util = new Util();
  }


  @Test
  public void testSplitData() throws IOException {
    byte[] data = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    int expectedLength = data.length / DATASIZE + (data.length % DATASIZE > 0 ? 1 : 0);
    ArrayList<byte[]> dataList = util.splitData(data, DATASIZE);
    assertEquals(expectedLength, dataList.size());
  }


  @Test
  public void testSafeFile() throws IOException {
    util.removeFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf");
    byte[] data = util.loadFile(filepath);
    util.safeFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf", data);
    byte[] data2 = util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf");
    assertArrayEquals(data, data2);
  }

  @Test
  public void testOverwriteFile() throws IOException {
    testSafeFile();
  }
}

