package com.nedap.university;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.university.util.DatagramProperties;
import com.nedap.university.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUtilities {
  Util util;

  String directoryPath = "/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/";
  String filepath = directoryPath + "tiny.pdf";

  @BeforeEach
  public void setUp() {
    util = new Util();
  }


  @Test
  public void testSplitData() throws IOException {
    byte[] data = Util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/tiny.pdf");
    int expectedLength = data.length / DatagramProperties.DATA_SIZE
        + (data.length % DatagramProperties.DATA_SIZE > 0 ? 1 : 0);
    ArrayList<byte[]> dataList = util.splitData(data);
    assertEquals(expectedLength, dataList.size());
  }


  @Test
  public void testSafeOverWriteFile() throws IOException {
    util.removeFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf");
    byte[] data = Util.loadFile(filepath);
    util.safeFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf", data);
    byte[] data2 = Util.loadFile("/home/Thomas.Hordijk/Documents/Nedap/Project_Module_2/my_git/Module_2_Final_Project/example_files/saved/tiny.pdf");
    assertArrayEquals(data, data2);
  }

  @Test
  public void testGetFiles() throws IOException {
    ArrayList<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("tiny.pdf");
    expectedFiles.add("medium.pdf");
    expectedFiles.add("large.pdf");

    ArrayList<String> files = util.getFileList(directoryPath);

    expectedFiles.sort(String.CASE_INSENSITIVE_ORDER);
    files.sort(String.CASE_INSENSITIVE_ORDER);

    assertEquals(expectedFiles, files);
  }
}

