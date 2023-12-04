package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

public interface UserService {

	List<UserResponseDto> getAllUsers();

	UserResponseDto createUser(UserRequestDto userRequestDto);

	List<TweetResponseDto> getFeed(String username);

	List<TweetResponseDto> getMentions(String username);

	List<UserResponseDto> getFollowing(String username);

	List<UserResponseDto> getFollowers(String username);

	void follow(CredentialsDto credentials, String username);

	void unfollow(CredentialsDto credentials, String username);

	UserResponseDto getUserByName(String username);

	UserResponseDto deleteUser(String username, CredentialsDto credentials);

	UserResponseDto updateUser(String username, UserRequestDto userRequestDto);

}
