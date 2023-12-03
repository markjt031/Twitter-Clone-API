package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

  private final HashtagRepository hashtagRepository;
  private final HashtagMapper hashtagMapper;

  private final TweetRepository tweetRepository;
  private final TweetMapper tweetMapper;

  @Override
  public List<HashtagDto> getAllHashtags() {
    return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
  }

  private String prependHashtag(String label) {
    return "#" + label;
  }

  @Override
  public List<TweetResponseDto> getTweetsByHashtagLabel(String label) {
    if (!hashtagExists(label)) {
      throw new NotFoundException("No hashtag with label: " + label);
    }

    return tweetMapper.entitiesToDtos(tweetRepository.findAllByContentContainingAndDeletedFalseOrderByPostedDesc(prependHashtag(label)));
  }

  @Override
  public boolean hashtagExists(String label) {
    return hashtagRepository.findByLabel(prependHashtag(label)).isPresent();
  }

}
