package com.davidmoreen.Model;

public class Contact {
  public int id;
  public String firstName;
  public String lastName;
  public String emailAddress;

  public Contact(int id, String firstName, String lastName, String emailAddress) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.emailAddress = emailAddress;
  }
}