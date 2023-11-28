package com.cooksys.socialmedia.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Tweet {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "author")
	private User author;

	@CreationTimestamp
	private Date posted;

	private boolean deleted;

	private String content;

	//a many to one and one to many relation where tweet is created to reply to an exisitng tweet
	@ManyToOne
	@JoinColumn(name = "tweet_reply")
	private Tweet inReplyTo;
	
	//this holds a list of tweets replied to this.tweet
	@OneToMany(mappedBy = "inReplyTo")
	private List<Tweet> replies = new ArrayList<>();

	//makes a tweet whenever it is reposted and adds to the list reposts below
	@ManyToOne
	@JoinColumn(name = "tweet_repost")
	private Tweet repostOf;
	
	//this list creates a list and adds the same tweet every time it is reposted
	@OneToMany(mappedBy = "repostOf")
	private List<Tweet> reposts = new ArrayList<>();

	//creates the join table where tweets and hashtags are linked, this creates a list of hashtags that are on this.tweet
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "tweet_hashtag", joinColumns = { @JoinColumn(name = "hashtag_ig") }, inverseJoinColumns = {
			@JoinColumn(name = "tweet_id") })
	private List<Hashtag> hashtags = new ArrayList<>();
	
	
	//many to many declarations of the join tables created in User.java
	
	@ManyToMany(mappedBy="userLikes")
	private List<User> likes = new ArrayList<>();
	
	@ManyToMany(mappedBy="userMentions")
	private List<User> mentions = new ArrayList<>();
}