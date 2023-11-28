package com.cooksys.socialmedia.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

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

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "username", column = @Column(nullable = false, unique = true)),
			@AttributeOverride(name = "password", column = @Column(nullable = false)) })
	private Credential credential;

	@CreationTimestamp
	private Date joined;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "firstName", column = @Column(nullable = false)),
			@AttributeOverride(name = "lastName", column = @Column(nullable = false)),
			@AttributeOverride(name = "email", column = @Column(nullable = false)),
			@AttributeOverride(name = "phone", column = @Column(nullable = false)) })
	private Profile profile;

	@ManyToMany
	@JoinTable(name = "following_table", joinColumns = { @JoinColumn(name = "follower_id") }, inverseJoinColumns = {
			@JoinColumn(name = "following_id") })
	private List<User> following = new ArrayList<>();

	@ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
	private List<User> followers = new ArrayList<>();

}
