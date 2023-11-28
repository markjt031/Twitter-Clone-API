package com.cooksys.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.entities.Credential;

@Mapper(componentModel ="spring")
public interface CredentialsMapper {
	CredentialsDto entityToDto(Credential entity);
	Credential credentialDtoToEntity(CredentialsDto credentialsDto);
}
