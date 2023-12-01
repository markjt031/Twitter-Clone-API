package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

  Tweet findByIdAndDeletedFalse(Long id);

  List<Tweet> findAllByContentContainingAndDeletedFalseOrderByPostedDesc(String content);
  
  List<Tweet> findAllByContentNotNullAndDeletedFalseOrderByPostedDesc();

  List<Tweet> findAllByDeletedFalseAndAuthorCredentialsUsernameInOrderByPostedDesc(Set<String> usernames);
  
  
}
