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

	@ManyToOne
	@JoinColumn(name = "tweet_reply")
	private Tweet inReplyTo;

	@ManyToOne
	@JoinColumn(name = "tweet_repost")
	private Tweet repostOf;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "tweet_hashtag", joinColumns = { @JoinColumn(name = "hashtag_ig") }, inverseJoinColumns = {
			@JoinColumn(name = "tweet_id") })
	private List<Hashtag> hashtags = new ArrayList<>();
	
	@ManyToMany(mappedBy="userLikes")
	private List<User> likes = new ArrayList<>();
	
	@ManyToMany(mappedBy="userMentions")
	private List<User> mentions = new ArrayList<>();
}
