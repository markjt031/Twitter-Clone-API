package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

  private final TweetService tweetService;

  @GetMapping("/{id}/tags")
  public List<HashtagDto> getTags(@PathVariable Long id) {
    return tweetService.getTags(id);
  }

  @GetMapping("/{id}/context")
  public ContextDto getContext(@PathVariable Long id) {
    return tweetService.getContext(id);
  }

  @GetMapping("/{id}/replies")
  public List<TweetResponseDto> getReplies(@PathVariable Long id) {
    return tweetService.getReplies(id);
  }

  @GetMapping("/{id}/reposts")
  public List<TweetResponseDto> getReposts(@PathVariable Long id) {
    return tweetService.getReposts(id);
  }

  @PostMapping("/{id}/reply")
  public TweetResponseDto createReply(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
    return tweetService.createReply(id, tweetRequestDto);
  }
}
