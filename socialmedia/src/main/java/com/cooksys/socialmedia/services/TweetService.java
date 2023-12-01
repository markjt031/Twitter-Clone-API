package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;

import java.util.List;

public interface TweetService {

  List<HashtagDto> getTags(Long id);

  ContextDto getContext(Long id);

  List<TweetResponseDto> getReplies(Long id);

  List<TweetResponseDto> getReposts(Long id);
}
