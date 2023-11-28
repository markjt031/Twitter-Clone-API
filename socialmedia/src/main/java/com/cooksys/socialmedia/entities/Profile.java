package com.cooksys.socialmedia.entities;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

}
