package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

  private final UserRepository userRepository;
  private final HashtagRepository hashtagRepository;

  @Override
  public boolean usernameExists(String username) {
    return userRepository.findByCredentialsUsernameIgnoreCase(username).isPresent();
  }

  @Override
  public boolean usernameAvailable(String username) {
    return !usernameExists(username);
  }

  @Override
  public boolean hashtagExists(String label) {
    return hashtagRepository.findByLabel("#" + label).isPresent();
  }
}
