package com.nedap.university.util;

import com.nedap.university.Communication.Window;
import com.nedap.university.Packets.InterfacePacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

  DatagramSocket socket;
  Window window;


  public Timeout(Window window) {
    this.window = window;
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
    if (window.getAcknowledgedPackets().contains(tag)) {
      window.removeAcknowledgedPacket(tag);
    } else {
      DatagramPacket newDatagram = new DatagramPacket(tag.getData(), tag.getData().length, tag.getAddress(), tag.getPort());
      socket.send(newDatagram);
      createTimer(tag, new Timer(), socket);
    }
  }

}
