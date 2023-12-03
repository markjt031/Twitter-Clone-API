package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
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
	private final CredentialsMapper credentialsMapper;
	
	
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
		String username= userToCreate.getCredentials().getUsername();
		if (userRepository.findByCredentialsUsername(username).isPresent()) {
			User foundUser=userRepository.findByCredentialsUsername(username).get();
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
		//creating list of usernames(user and all follows) to pass into derived query
		User user = getUser(username);
		Set<String> usernames = new HashSet<String>();
		usernames.add(user.getCredentials().getUsername());
		for (User following : user.getFollowing()) {
			usernames.add(following.getCredentials().getUsername());
		}
		
		return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalseAndAuthorCredentialsUsernameInOrderByPostedDesc(usernames));
	}


	@Override
	public List<TweetResponseDto> getMentions(String username) {
		List<Tweet> mentions = new ArrayList<>();
		if (checkExisting(username)) {
			List<Tweet> tweets = tweetRepository.findAllByContentNotNullAndDeletedFalseOrderByPostedDesc();
			
			for (Tweet t : tweets) {
				
				for (User u: t.getMentions()) {
					if (u.getCredentials().getUsername().equals(username)) {
						mentions.add(t);
					}
				
				}
			}
			
		}
		else throw new NotFoundException("user not found");
		return tweetMapper.entitiesToDtos(mentions);
	}


	@Override
	public List<UserResponseDto> getFollowing(String username) {
		User user = getUser(username);
		List<User> allFollowing= user.getFollowing();
		List<User> activeUsersFollowing = new ArrayList<>();
		for (User u: allFollowing) {
			if (u.isDeleted()==false) {
				activeUsersFollowing.add(u);
			}
		}
		return userMapper.entitiesToDtos(activeUsersFollowing);
	}

	@Override
	public List<UserResponseDto> getFollowers(String username) {
		User user = getUser(username);
		List<User> allFollowers= user.getFollowers();
		List<User> activeUserFollowers = new ArrayList<>();
		for (User u: allFollowers) {
			if (u.isDeleted()==false) {
				activeUserFollowers.add(u);
			}
		}
		return userMapper.entitiesToDtos(activeUserFollowers);
	}
	
	@Override
	public void follow(CredentialsDto credentials, String username) {
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(credentials);
		if (checkCredentials(userCredentials) && checkExisting(username)) {
			User followingUser = getUser(userCredentials.getUsername());
			User followedUser = getUser(username);
			if (followingUser.getFollowing().contains(followedUser) && followedUser.getFollowers().contains(followingUser)) {
				throw new BadRequestException("following relationship already exists");
			}
			followingUser.getFollowing().add(followedUser);
			followedUser.getFollowers().add(followingUser);
			userRepository.saveAllAndFlush(Arrays.asList(followedUser, followingUser));
		}
		else throw new NotFoundException("user requested to follow does not exist");
		
	}

	@Override
	public void unfollow(CredentialsDto credentials, String username) {
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(credentials);
		if (checkCredentials(userCredentials) && checkExisting(username)) {
			User followingUser = getUser(userCredentials.getUsername());
			User unfollowedUser = getUser(username);
			if (followingUser.getFollowing().contains(unfollowedUser)==false && unfollowedUser.getFollowers().contains(unfollowedUser)==false) {
				throw new BadRequestException("following relationship does not exist");
			}
			followingUser.getFollowing().remove(unfollowedUser);
			unfollowedUser.getFollowers().remove(followingUser);
			userRepository.saveAllAndFlush(Arrays.asList(unfollowedUser, followingUser));
		}
		else throw new NotFoundException("user to unfollow does not exist");
	}
	
	public User getUser(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
		if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
			throw new NotFoundException("user not found");
		}
		else return optionalUser.get();
	}
	
	public boolean checkExisting(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
		if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
			return false;
		}
		return true;
	}

	public boolean checkCredentials(Credentials credentials) {
		Optional<User> optionalUser = userRepository.findByCredentials(credentials);
		if (credentials == null || optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
			throw new NotAuthorizedException("credentials do not match existing user");
		}
		return true;
	}


	
	

}
