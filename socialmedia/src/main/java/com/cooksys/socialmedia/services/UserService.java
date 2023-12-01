package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

public interface UserService {

	List<UserResponseDto> getAllUsers();

	UserResponseDto createUser(UserRequestDto userRequestDto);

	UserResponseDto getUserByName(String username);
	
	
}
