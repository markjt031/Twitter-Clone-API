package com.cooksys.socialmedia.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	
	
	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse().get());
		 
	}


	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		if (userRequestDto == null || userRequestDto.getCredentials()==null || userRequestDto.getCredentials().getUsername()==null
				|| userRequestDto.getCredentials().getPassword()==null || userRequestDto.getProfile()== null || userRequestDto.getProfile().getEmail()==null) {
			throw new BadRequestException("include all required fields");
		}
		User userToCreate = userMapper.requestDtoToEntity(userRequestDto);
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(userToCreate.getCredentials().getUsername());
		if (optionalUser.isPresent()) {
			User foundUser=optionalUser.get();
			if (foundUser.isDeleted()==true) {
				foundUser.setDeleted(false);
				return userMapper.entityToDto(userRepository.saveAndFlush(foundUser));
			}
			else throw new BadRequestException("user already exists");
		}
		return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));
	}


	@Override
	public List<TweetResponseDto> getFeed(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
		if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
			throw new NotFoundException("user not found");
		}
		//creating list of usernames(user and all follows) to pass into derived query
		User user = optionalUser.get();
		Set<String> usernames = new HashSet<String>();
		usernames.add(user.getCredentials().getUsername());
		for (User following : user.getFollowing()) {
			usernames.add(following.getCredentials().getUsername());
		}
//		
		return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalseAndAuthorCredentialsUsernameInOrderByPostedDesc(usernames));
	}
}
