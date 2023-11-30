package com.cooksys.socialmedia.entities;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Credential{
	private String username;
	private String password;
}
