package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;

import java.util.List;

public interface TweetService {

  public List<TweetResponseDto> getAllTweetsByLabelNotDeleted(String label);

}
