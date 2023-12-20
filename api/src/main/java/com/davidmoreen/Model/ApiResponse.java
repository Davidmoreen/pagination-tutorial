package com.davidmoreen.Model;

import java.util.List;
import java.util.Optional;

public class ApiResponse {
  public Integer hasMoreAfter;
  public List<Contact> contacts;

  public ApiResponse(Integer hasMoreAfter, List<Contact> contacts) {
    this.hasMoreAfter = hasMoreAfter;
    this.contacts = contacts;
  }
}
