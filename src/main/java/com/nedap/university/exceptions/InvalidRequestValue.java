package com.nedap.university.exceptions;

public class InvalidRequestValue extends Exception {
  public InvalidRequestValue(String message) {
    System.out.println(message);
  }

  public InvalidRequestValue() {
    System.out.println("The request value does not conform to a valid request type");
  }
}
