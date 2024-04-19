package com.nedap.university.Communication;

import com.nedap.university.exceptions.InvalidRequestValue;

public enum Requests {
  LIST(1), // List all the files on the server
  UPLOAD(2), // Indicate the start of an upload
  DOWNLOAD(3), // Download a file
  REMOVE(4),
  RENAME(5),
  CONNECT(9),
  EXIT(10),
  ERROR(11),
  HELP(-1),
  EMPTY(0);

  private int value;

  Requests(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static Requests byValue(int value) throws InvalidRequestValue {
    for(Requests request : Requests.values()) {
      if(request.getValue() == value) {
        return request;
      }
    }
    throw new InvalidRequestValue("The value " + value + " is not a valid request");
  }

  public static boolean validRequest(String request) {
    return switch (request) {
      case "CONNECT", "UPLOAD", "DOWNLOAD", "LIST", "REMOVE", "RENAME", "EXIT", "EMPTY" -> true;
      default -> false;
    };
  }
}
