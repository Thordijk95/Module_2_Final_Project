package com.nedap.university.Exceptions;

public class IncorrectArgumentException extends Exception {
  public IncorrectArgumentException(String message) {
    System.out.println(message);
  }
   public IncorrectArgumentException() {
     System.out.println("The arguments provided are not correct!");
   }
}
