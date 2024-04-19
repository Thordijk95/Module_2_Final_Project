package com.nedap.university.util;

import com.nedap.university.Communication.SlidingWindow;
import com.nedap.university.Packets.InterfacePacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

  DatagramSocket socket;
  SlidingWindow slidingWindow;


  public Timeout(SlidingWindow sw) {
    slidingWindow = sw;
  }

  public void createTimer(InterfacePacket tag, Timer timer, DatagramSocket socket) {
    this.socket = socket;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          TimeoutElapsed(tag);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }, DatagramProperties.TIMEOUT);
  }

  private void TimeoutElapsed(InterfacePacket tag) throws IOException {
    if (slidingWindow.getAcknowledgedPackets().contains(tag)) {
      slidingWindow.removeAcknowledgedPacket(tag);
    } else {
      System.out.println("Sending from timeout!!!");
      DatagramPacket newDatagram = new DatagramPacket(tag.getData(), tag.getData().length, tag.getAddress(), tag.getPort());
      socket.send(newDatagram);
      createTimer(tag, new Timer(), socket);
    }
  }

}
