package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.controllers.USerResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

public interface UserService {

	List<UserResponseDto> getAllUsers();

	UserResponseDto createUser(UserRequestDto userRequestDto);

	List<TweetResponseDto> getFeed(String username);

	List<TweetResponseDto> getMentions(String username);

	List<UserResponseDto> getFollowing(String username);

	List<UserResponseDto> getFollowers(String username);
	
	
}
