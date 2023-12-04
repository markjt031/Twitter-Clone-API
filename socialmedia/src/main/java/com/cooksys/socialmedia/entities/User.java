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
import javax.persistence.OneToMany;
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

	//embedding the credential embeddable from Credential.java
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "username", column = @Column(nullable = false, unique = true)),
			@AttributeOverride(name = "password", column = @Column(nullable = false)) })
	private Credentials credentials;

	@CreationTimestamp
	private Date joined;

	private boolean deleted;
	//embedding the Profile from Profile.java
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "firstName", column = @Column(nullable = false)),
			@AttributeOverride(name = "lastName", column = @Column(nullable = false)),
			@AttributeOverride(name = "email", column = @Column(nullable = false)),
			@AttributeOverride(name = "phone", column = @Column(nullable = false)) })
	private Profile profile;

	
	//all the many to many relations are declared below
	
	
	//Creates the follower relation table and makes a list of users who are being followed by this.user
	@ManyToMany
	@JoinTable(name = "following_table", joinColumns = { @JoinColumn(name = "follower_id") }, inverseJoinColumns = {
			@JoinColumn(name = "following_id") })
	private List<User> following = new ArrayList<>();

	//creates a list of users who are the followers of this.user
	@ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
	private List<User> followers = new ArrayList<>();

	//Creates a list of tweets by this user
	@OneToMany(mappedBy = "author")
	private List<Tweet> tweets = new ArrayList<>();

	
	//Creates the user_likes relational tables and creates a List of tweets this user likes
	@ManyToMany
	@JoinTable(name = "user_likes", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "tweet_id") })
	private List<Tweet> userLikes = new ArrayList<>();
	
	//Creates the user_mention relational table and creates a list of tweets where the user is mentioned
	@ManyToMany(mappedBy ="mentions")
	private List<Tweet> userMentions = new ArrayList<>();
	
	public void likeTweet(Tweet tweet) {
        this.userLikes.add(tweet);
        tweet.getLikes().add(this);
    }
}
