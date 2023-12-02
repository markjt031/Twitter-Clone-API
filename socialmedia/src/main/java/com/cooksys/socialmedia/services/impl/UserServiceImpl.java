package com.cooksys.socialmedia.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse().get());

	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		if (userRequestDto == null || userRequestDto.getCredentials() == null
				|| userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getCredentials().getPassword() == null || userRequestDto.getProfile() == null
				|| userRequestDto.getProfile().getEmail() == null) {
			throw new BadRequestException("include all required fields");
		}
		User userToCreate = userMapper.requestDtoToEntity(userRequestDto);
		Optional<User> optionalUser = userRepository
				.findByCredentialsUsername(userToCreate.getCredentials().getUsername());
		if (optionalUser.isPresent()) {
			User foundUser = optionalUser.get();
			if (foundUser.isDeleted() == true) {
				foundUser.setDeleted(false);
				return userMapper.entityToDto(userRepository.saveAndFlush(foundUser));
			} else
				throw new BadRequestException("user already exists");
		}
		return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));
	}

	// gets an existing user by username
	@Override
	public UserResponseDto getUserByName(String username) {
		Optional<User> userOptional = userRepository.findByCredentialsUsername(username);

	    if (userOptional.isPresent()) {
	        User foundUser = userOptional.get();
	        if (foundUser == null || foundUser.isDeleted()) {
	            throw new NotFoundException("User not found");
	        }

	        return userMapper.entityToDto(foundUser);
	    } else {
	        throw new NotFoundException("User not found");
	    }
	}

	// deletes user by username
	@Override
	public UserResponseDto deleteUser(String username) {
		Optional<User> userOptional = userRepository.findByCredentialsUsername(username);

	    if (userOptional.isPresent()) {
	        User foundUser = userOptional.get();
	        if (foundUser == null || foundUser.isDeleted()) {
	            throw new NotFoundException("User not found");
	        }

	        foundUser.setDeleted(true);
	        return userMapper.entityToDto(userRepository.saveAndFlush(foundUser));
	    } else {
	        throw new NotFoundException("User not found");
	    }
	}
}
