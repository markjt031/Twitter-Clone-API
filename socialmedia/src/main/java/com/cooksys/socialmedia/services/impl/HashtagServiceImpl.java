package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.services.HashtagService;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

  private final HashtagRepository hashtagRepository;
  private final HashtagMapper hashtagMapper;
  private final TweetService tweetService;

  @Override
  public List<HashtagDto> getAllHashtags() {
    return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
  }

  private String prependHashtag(String label) {
    return "#" + label;
  }

  private boolean hashtagExists(String label) {
    return hashtagRepository.findByLabel(prependHashtag(label)) != null;
  }

  @Override
  public List<TweetResponseDto> getTweetsByHashtagLabel(String label) {
    if (!hashtagExists(label)) {
      throw new NotFoundException("No hashtag with label: " + label);
    }

    return tweetService.getAllTweetsByLabelNotDeleted(label);
  }
}
