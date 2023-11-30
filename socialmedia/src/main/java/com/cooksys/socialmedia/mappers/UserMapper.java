package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, CredentialsMapper.class })
public interface UserMapper {

	@Mapping(target = "username", source = "credentials.username")
	UserResponseDto entityToDto(User entity);

	List<UserResponseDto> entitiesToDtos(List<User> entities);

	User requestDtoToEntity(UserRequestDto userRequestDto);
}
