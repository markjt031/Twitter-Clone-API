package com.cooksys.socialmedia.services.impl;


import com.cooksys.socialmedia.dtos.*;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;


import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final UserRepository userRepository;

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


    if (tweet.getReplies() != null) {
      for (Tweet reply : tweet.getReplies()) {
        chain.add(reply);
        chain.addAll(getAfterInReplyToChain(reply));
      }
    }

		if (tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " does not exist");
		}


		List<Tweet> replies = tweet.getReplies();
		replies.sort(Comparator.comparing(Tweet::getPosted));
		replies.removeIf(Tweet::isDeleted);
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
    return hashtagMapper.entitiesToDtos(tweet.getHashtags());
  }

		return tweetMapper.entitiesToDtos(replies);
	}

	@Override
	public List<TweetResponseDto> getReposts(Long id) {
		Tweet tweet = getTweet(id);

		if (tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " does not exist");
		}


		List<Tweet> reposts = tweet.getReposts();
		reposts.sort(Comparator.comparing(Tweet::getPosted));
		reposts.removeIf(Tweet::isDeleted);


    List<Tweet> before = getBeforeInReplyToChain(tweet);
    before.sort(Comparator.comparing(Tweet::getPosted));
    before.removeIf(Tweet::isDeleted);

    List<Tweet> after = new ArrayList<>(getAfterInReplyToChain(tweet));
    after.sort(Comparator.comparing(Tweet::getPosted));
    after.removeIf(Tweet::isDeleted);

		return tweetMapper.entitiesToDtos(reposts);
	}

//returns all non deleted tweets
	@Override
	public List<TweetResponseDto> getAllTweets() {
		List<Tweet> allTweets = tweetRepository.findAll();
		List<TweetResponseDto> result = new ArrayList<>();


		for (Tweet tweet : allTweets) {
			if (!tweet.isDeleted()) {
				result.add(tweetMapper.entityToDto(tweet));
			}
		}

		return result;
	}


    List<Tweet> replies = tweet.getReplies();
    replies.sort(Comparator.comparing(Tweet::getPosted));
    replies.removeIf(Tweet::isDeleted);
=======
//returns an existing tweet by its id
	@Override
	public TweetResponseDto getTweetById(Long id) {
		Tweet tweet = getTweet(id);

		if (tweet == null || tweet.isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}

		return tweetMapper.entityToDto(tweet);
	}


//gets all the non deleted tweet by a user
	@Override
	public List<TweetResponseDto> getUserTweets(String username) {
		Optional<User> userOptional = userRepository.findByCredentialsUsername(username);

	    if (userOptional.isPresent()) {
	        User foundUser = userOptional.get();

	        if (foundUser.isDeleted()) {
	            throw new NotFoundException("User not found");
	        }


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

	        List<TweetResponseDto> result = new ArrayList<>();
	        for (Tweet t : foundUser.getTweets()) {
	            if (!t.isDeleted()) {
	                result.add(tweetMapper.entityToDto(t));
	            }
	        }

	        return result;

	    } else {
	        throw new NotFoundException("User not found");
	    }
	}


}
