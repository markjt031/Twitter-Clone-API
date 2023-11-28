package com.cooksys.socialmedia.dtos;

import java.util.Date;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetResponseDto {
	private Long id;
	private UserResponseDto author;
	private Date posted;
	private String content;
	private TweetResponseDto inReplyTo;
	private TweetResponseDto repostOf;
}
