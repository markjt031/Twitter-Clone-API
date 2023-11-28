package com.cooksys.socialmedia.dtos;

import java.util.Date;

import com.cooksys.socialmedia.entities.Profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {
	private String username;
	private Profile profile;
	private Date joined;
}
