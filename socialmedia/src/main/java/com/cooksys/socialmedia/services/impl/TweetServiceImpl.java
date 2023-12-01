package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

  private final TweetRepository tweetRepository;
  private final TweetMapper tweetMapper;

  private final HashtagMapper hashtagMapper;

  private Tweet getTweet(Long id) {
    return tweetRepository.findByIdAndDeletedFalse(id);
  }

  private List<Tweet> getBeforeInReplyToChain(Tweet tweet) {
    List<Tweet> chain = new ArrayList<>();

    while (tweet.getInReplyTo() != null) {
      chain.add(tweet.getInReplyTo());
      tweet = tweet.getInReplyTo();
    }

    return chain;
  }

  private Set<Tweet> getAfterInReplyToChain(Tweet tweet) {
    Set<Tweet> chain = new HashSet<>();

    // solve without recursion
    if (tweet.getReplies() != null) {
      for (Tweet reply : tweet.getReplies()) {
        chain.add(reply);
        chain.addAll(getAfterInReplyToChain(reply));
      }
    }

    return chain;
  }

  @Override
  public List<HashtagDto> getTags(Long id) {
    Tweet tweet = getTweet(id);

    if (tweet == null) {
      throw new NotFoundException("Tweet with id " + id + " does not exist");
    }

    return hashtagMapper.entitiesToDtos(tweet.getHashtags());
  }

  @Override
  public ContextDto getContext(Long id) {
    Tweet tweet = getTweet(id);

    if (tweet == null) {
      throw new NotFoundException("Tweet with id " + id + " does not exist");
    }

    List<Tweet> before = getBeforeInReplyToChain(tweet);
    before.sort(Comparator.comparing(Tweet::getPosted));
    before.removeIf(Tweet::isDeleted);
    List<Tweet> after = new ArrayList<>(getAfterInReplyToChain(tweet));
    after.sort(Comparator.comparing(Tweet::getPosted));
    after.removeIf(Tweet::isDeleted);

    return tweetMapper.entityToContextDto(tweet, before, after);
  }

  @Override
  public List<TweetResponseDto> getReplies(Long id) {
    Tweet tweet = getTweet(id);

    if (tweet == null) {
      throw new NotFoundException("Tweet with id " + id + " does not exist");
    }

    List<Tweet> replies = tweet.getReplies();
    replies.sort(Comparator.comparing(Tweet::getPosted));
    replies.removeIf(Tweet::isDeleted);

    return tweetMapper.entitiesToDtos(replies);
  }
}
