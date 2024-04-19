package com.nedap.university.util.Window;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class SlidingWindow {

  ArrayList<DatagramPacket> acknowledgedPackets;

  public SlidingWindow() {
    acknowledgedPackets = new ArrayList<>();
  }

  public void addAcknowledgedPacket(DatagramPacket packet) {
    acknowledgedPackets.add(packet);
  }

  public void removeAcknowledgedPacket(DatagramPacket packet) {
    acknowledgedPackets.remove(packet);
  }

  public ArrayList<DatagramPacket> getAcknowledgedPackets() {
    return acknowledgedPackets;
  }

  public void sender() {

  }

}
