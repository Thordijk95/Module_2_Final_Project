package com.nedap.university.Communication;

import com.nedap.university.util.Conversions;
import com.nedap.university.util.DatagramProperties;
import com.nedap.university.Packets.InboundPacket;
import com.nedap.university.Packets.InterfacePacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class ReceiveWindow {

  ArrayList<InterfacePacket> receiveWindow;


  public void receiver(DatagramSocket socket, Requests requestsType) throws IOException {
    while(true) {
      DatagramPacket inboundDatagramPacket = new DatagramPacket(new byte[DatagramProperties.DATA_SIZE], DatagramProperties.DATA_SIZE);
      socket.receive(inboundDatagramPacket);
      InterfacePacket inboundPacket = new InboundPacket(inboundDatagramPacket);
      System.out.println("Received a packet after acknowledgement");
      if (inboundPacket.getRequestType() == requestsType) {
        // The request for the list has been acknowledged, the list of files is contained in data
        String[] fileList = Conversions.fromByteArrayToStringArray(inboundPacket.getData(),
            DatagramProperties.SEPARATOR.toString());
        System.out.println(Arrays.toString(fileList));
        for (String file : fileList) {
          if (file.isEmpty()) {
            // end  of list has been reached stop waiting for more lists
            return;
          }
          System.out.println(file);
        }
      } else {
        System.out.println(inboundPacket.getRequestType());
      }
    }
  }

}
