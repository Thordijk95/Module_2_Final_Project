package com.nedap.university.util;

import com.nedap.university.util.CommandHandler.CommandHandler;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

  DatagramSocket socket;
  SlidingWindow slidingWindow;


  public Timeout(SlidingWindow sw) {
    slidingWindow = sw;
  }

  public void createTimer(DatagramPacket tag, Timer timer, DatagramSocket socket) {
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

  private void TimeoutElapsed(DatagramPacket tag) throws IOException {
    ArrayList<DatagramPacket> packets = slidingWindow.getAcknowledgedPackets();
    if (packets.contains(tag)) {
      packets.remove(tag);
    } else {
      System.out.println("Sending from timeout!!!");
      socket.send(tag);
      createTimer(tag, new Timer(), socket);
    }

  }

}
