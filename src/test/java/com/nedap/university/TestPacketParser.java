package com.nedap.university;

import com.nedap.university.util.PacketParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPacketParser {
  PacketParser parser;

  @BeforeEach
  public void setup() {
    parser = new PacketParser();
  }
//
//  @Test
//  public void testParser() {
//    parser.evaluateChecksum();
//
//  }
}
