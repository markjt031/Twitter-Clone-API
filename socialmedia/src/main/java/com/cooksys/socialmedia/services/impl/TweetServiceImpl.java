package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.*;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

  private final TweetRepository tweetRepository;
  private final TweetMapper tweetMapper;

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private final HashtagMapper hashtagMapper;
  private final HashtagRepository hashtagRepository;

  private Tweet getTweet(Long id) {
    Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

    if (optionalTweet.isEmpty()) {
      throw new NotFoundException("Tweet with id " + id + " does not exist");
    }

    return optionalTweet.get();
  }

  private boolean validateTweetRequest(TweetRequestDto tweetRequestDto) {
    if (tweetRequestDto == null) {
      return false;
    }
    if (tweetRequestDto.getContent() == null) {
      return false;
    }
    if (tweetRequestDto.getCredentials() == null) {
      return false;
    }
    if (tweetRequestDto.getCredentials().getUsername() == null) {
      return false;
    }
    if (tweetRequestDto.getCredentials().getPassword() == null) {
      return false;
    }
    return true;
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

    if (tweet.getReplies() != null) {
      for (Tweet reply : tweet.getReplies()) {
        chain.add(reply);
        chain.addAll(getAfterInReplyToChain(reply));
      }
    }

    return chain;
  }

  private void parseAndAddHashtags(Tweet tweet) {
    String content = tweet.getContent();
    String[] words = content.split(" ");
    for (String word : words) {
      if (word.startsWith("#")) {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(word);
        Hashtag hashtag;
        if (optionalHashtag.isEmpty()) {
          hashtag = new Hashtag();
          hashtag.setLabel(word);
          hashtag.getTweets().add(tweet);
          hashtagRepository.saveAndFlush(hashtag);
        } else {
          hashtag = optionalHashtag.get();
        }
        tweet.getHashtags().add(hashtag);
        tweetRepository.saveAndFlush(tweet);
      }
    }
  }

  private void parseAndAddMentions(Tweet tweet) {
    String content = tweet.getContent();
    String[] words = content.split(" ");
    for (String word : words) {
      if (word.startsWith("@")) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameIgnoreCase(word.substring(1));
        if (optionalUser.isPresent()) {
          User user = optionalUser.get();
          user.getUserMentions().add(tweet);
          userRepository.saveAndFlush(user);
          tweet.getMentions().add(user);
          tweetRepository.saveAndFlush(tweet);
        }
      }
    }
  }

  @Override
  public List<HashtagDto> getTags(Long id) {
    Tweet tweet = getTweet(id);
    List<Hashtag> hashtags = tweet.getHashtags();

    for (Hashtag hashtag : hashtags) {
      hashtag.setLabel(hashtag.getLabel().substring(1));
    }

    return hashtagMapper.entitiesToDtos(tweet.getHashtags());
  }

  @Override
  public ContextDto getContext(Long id) {
    Tweet tweet = getTweet(id);

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

    List<Tweet> replies = tweet.getReplies();
    replies.sort(Comparator.comparing(Tweet::getPosted));
    replies.removeIf(Tweet::isDeleted);

    return tweetMapper.entitiesToDtos(replies);
  }

  @Override
  public List<TweetResponseDto> getReposts(Long id) {
    Tweet tweet = getTweet(id);

    List<Tweet> reposts = tweet.getReposts();
    reposts.sort(Comparator.comparing(Tweet::getPosted));
    reposts.removeIf(Tweet::isDeleted);

    return tweetMapper.entitiesToDtos(reposts);
  }

  @Override
  public List<UserResponseDto> getLikes(Long id) {
    Tweet tweet = getTweet(id);
    List<User> likes = new ArrayList<>();
    for (User u : tweet.getLikes()) {
      if (u.isDeleted() == false) {
        likes.add(u);
      }
    }
    return userMapper.entitiesToDtos(likes);
  }

  //Helper method for finding @mentions from text
  public Set<String> findMentions(String tweetContent) {
    if (tweetContent == null) {
      return null;
    }
    String mentionRegex = "@\\w+";
    Pattern pattern = Pattern.compile(mentionRegex);
    Matcher matcher = pattern.matcher(tweetContent);
    Set<String> mentions = new HashSet<>();
    // Find mentions
    while (matcher.find()) {
      String mention = matcher.group();
      if (mention != null) {
        mentions.add(mention);
      }
    }
    return mentions;
  }

  @Override
  public List<UserResponseDto> getMentions(Long id) {
    Tweet tweet = getTweet(id);
    List<User> mentionsIncludingDeleted = tweet.getMentions();
    List<User> mentions = new ArrayList<User>();
    for (User u : mentionsIncludingDeleted) {
      if (u.isDeleted() == false) {
        mentions.add(u);
      }
    }
    return userMapper.entitiesToDtos(mentions);
  }

  @Override
  public TweetResponseDto createReply(Long id, TweetRequestDto tweetRequestDto) {
    Tweet tweet = getTweet(id);

    if (!validateTweetRequest(tweetRequestDto)) {
      throw new BadRequestException("Tweet request is not valid");
    }

    Optional<User> optionalReplyAuthor = userRepository.findByCredentialsUsernameIgnoreCase(tweetRequestDto.getCredentials().getUsername());
    if (optionalReplyAuthor.isEmpty()) {
      throw new NotFoundException("User credentials is not found or incorrect");
    }
    User replyAuthor = optionalReplyAuthor.get();
    Tweet reply = tweetRepository.saveAndFlush(tweetMapper.requestDtoToEntity(tweetRequestDto));

    reply.setAuthor(replyAuthor);
    reply.setInReplyTo(tweet);
    reply = tweetRepository.saveAndFlush(reply);

    tweet.getReplies().add(reply);
    tweetRepository.saveAndFlush(tweet);

    parseAndAddHashtags(reply);
    parseAndAddMentions(reply);

    return tweetMapper.entityToDto(reply);
  }
}
