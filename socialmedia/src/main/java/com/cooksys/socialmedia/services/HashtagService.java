package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {
  
  List<HashtagDto> getAllHashtags();

  List<TweetResponseDto> getTweetsByHashtagLabel(String label);

  boolean hashtagExists(String label);

}
