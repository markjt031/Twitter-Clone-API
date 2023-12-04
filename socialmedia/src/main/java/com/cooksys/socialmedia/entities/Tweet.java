package com.cooksys.socialmedia.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
  @ManyToMany
  @JoinTable(name = "tweet_hashtag", joinColumns = {@JoinColumn(name = "tweet_id")}, inverseJoinColumns = {
      @JoinColumn(name = "hashtag_id")})
  private List<Hashtag> hashtags = new ArrayList<>();


  //many to many declarations of the join tables created in User.java

  @ManyToMany(mappedBy = "userLikes")
  private List<User> likes = new ArrayList<>();

  @ManyToMany
  @JoinTable(name = "user_mentions", joinColumns = {@JoinColumn(name = "tweet_id")}, inverseJoinColumns = {
      @JoinColumn(name = "user_id")})
  private List<User> mentions = new ArrayList<>();
  
  //public methods to help process adding of user mentions and hastags when a new tweet is created
  public void addMentionedUser(User user) {
      this.mentions.add(user);
      user.getUserMentions().add(this);
  }
  
  public void addHashtag(Hashtag hashtag) {
      this.hashtags.add(hashtag);
      hashtag.getTweets().add(this);
  }
}
