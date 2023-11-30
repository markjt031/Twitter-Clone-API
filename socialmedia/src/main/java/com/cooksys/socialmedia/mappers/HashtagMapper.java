package com.cooksys.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.entities.Hashtag;

import java.util.List;

@Mapper(componentModel="spring")
public interface HashtagMapper {
	HashtagDto entityToHashtagDto(Hashtag entity);
	Hashtag hashtagDtoToEntity(HashtagDto hashtagDto);
	List<HashtagDto> entitiesToDtos(List<Hashtag> all);
}
