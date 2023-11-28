package com.cooksys.socialmedia.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	@JoinColumn(name = "user_id")
	private User author;

	@Basic(optional = false)
	@Column(insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date posted;

	private boolean deleted;

	private String content;

	@ManyToOne
	@JoinColumn(name="tweet_reply")
	private Tweet inReplyTo;
	
	@ManyToOne
	@JoinColumn(name="tweet_repost")
	private Tweet repostOf;
}
