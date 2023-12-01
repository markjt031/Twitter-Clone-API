package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final HashtagMapper hashtagMapper;
	private final UserMapper userMapper;

	private Tweet getTweet(Long id) {
//    return tweetRepository.findByIdAndDeletedFalse(id);
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("Tweet with id " + id + " does not exist");
		}

		return optionalTweet.get();
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

		String mentionRegex = "@\\w+";
		Pattern pattern = Pattern.compile(mentionRegex);
		Matcher matcher = pattern.matcher(tweetContent);
		Set<String> mentions = new HashSet<>();
		// Find mentions
		while (matcher.find()) {
			String mention = matcher.group();
			mentions.add(mention);
		}
		return mentions;
	}

	@Override
	public List<UserResponseDto> getMentions(Long id) {
		Tweet tweet = getTweet(id);
		List<User> mentionsIncludingDeleted = tweet.getMentions();
		List<User> mentions = new ArrayList<User>();
		for (User u: mentionsIncludingDeleted) {
			if (u.isDeleted()==false) {
				mentions.add(u);
			}
		}
		return userMapper.entitiesToDtos(mentions);
	}
}
