package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.HashtagDto;

import java.util.List;

public interface TweetService {

  List<HashtagDto> getTags(Long id);

}
