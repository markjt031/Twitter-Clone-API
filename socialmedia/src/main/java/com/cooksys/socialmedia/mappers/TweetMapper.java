package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TweetMapper {
  TweetResponseDto entityToDto(Tweet entity);

  List<TweetResponseDto> entitiesToDtos(List<Tweet> entities);

  Tweet requestDtoToEntity(TweetRequestDto tweetRequestDto);

  ContextDto entityToContextDto(Tweet target, List<Tweet> before, List<Tweet> after);

}
