package com.cooksys.socialmedia.dtos;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HashtagDto {
	private String label;
	private Date firstUsed;
	private Date lastUsed;
}
