package com.cooksys.socialmedia.services;

public interface ValidateService {
  boolean usernameExists(String username);

  boolean usernameAvailable(String username);
}
