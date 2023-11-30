package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

  private final TweetRepository tweetRepository;

  private final HashtagMapper hashtagMapper;

  private Tweet getTweet(Long id) {
    return tweetRepository.findByIdAndDeletedFalse(id);
  }

  @Override
  public List<HashtagDto> getTags(Long id) {
    Tweet tweet = getTweet(id);

    if (tweet == null) {
      throw new NotFoundException("Tweet with id " + id + " does not exist");
    }

    return hashtagMapper.entitiesToDtos(tweet.getHashtags());
  }
}
