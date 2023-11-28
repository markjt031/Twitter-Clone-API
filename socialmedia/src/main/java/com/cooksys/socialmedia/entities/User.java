package com.cooksys.socialmedia.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "user_table")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	private String username;

	private String password;

	@Basic(optional = false)
	@Column(insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date joined;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;
}
