package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

  private final HashtagService hashtagService;

  @GetMapping
  public List<HashtagDto> getAllHashtags() {
    return hashtagService.getAllHashtags();
  }

  @GetMapping("/{label}")
  public List<TweetResponseDto> getTweetsByHashtagLabel(@PathVariable String label) {
    return hashtagService.getTweetsByHashtagLabel(label);
  }

  @GetMapping("/exists/{label}")
  public boolean hashtagExists(@PathVariable String label) {
    System.out.println(label);
    boolean exists = hashtagService.hashtagExists(label);
    System.out.println(exists);
    return hashtagService.hashtagExists(label);
  }
}


