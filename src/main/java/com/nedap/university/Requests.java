package com.nedap.university;

public enum Requests {
  LIST(1), // List all the files on the server
  UPLOAD(2), // Indicate the start of an upload
  DOWNLOAD(3), // Download a file
  REMOVE(4),
  RENAME(5),
  EXIT(10),
  EMPTY(0);

  private int value;

  Requests(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static boolean validRequest(String request) {
    return switch (request) {
      case "UPLOAD", "DOWNLOAD", "LIST", "REMOVE", "RENAME", "EXIT" -> true;
      default -> false;
    };
  }
}
