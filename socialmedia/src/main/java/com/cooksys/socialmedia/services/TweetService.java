package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

public interface TweetService {

	List<HashtagDto> getTags(Long id);

	ContextDto getContext(Long id);

	List<TweetResponseDto> getReplies(Long id);

	List<TweetResponseDto> getReposts(Long id);

	List<TweetResponseDto> getAllTweets();

	TweetResponseDto getTweetById(Long id);


  List<TweetResponseDto> getReposts(Long id);

  List<UserResponseDto> getLikes(Long id);

  List<UserResponseDto> getMentions(Long id);

  TweetResponseDto createReply(Long id, TweetRequestDto tweetRequestDto);

	List<TweetResponseDto> getUserTweets(String username);

}
