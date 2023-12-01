package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

  private final ValidateService validateService;

  @GetMapping("/username/exists/@{username}")
  public boolean usernameExists(@PathVariable String username) {
    return validateService.usernameExists(username);
  }

  @GetMapping("/username/available/@{username}")
  public boolean usernameAvailable(@PathVariable String username) {
    return validateService.usernameAvailable(username);
  }
}
