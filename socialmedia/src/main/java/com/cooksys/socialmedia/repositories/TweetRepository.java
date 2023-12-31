package com.cooksys.socialmedia.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.socialmedia.entities.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

	Optional<Tweet> findByIdAndDeletedFalse(Long id);

	List<Tweet> findAllByContentContainingAndDeletedFalseOrderByPostedDesc(String content);

	List<Tweet> findAllByContentNotNullAndDeletedFalseOrderByPostedDesc();

	List<Tweet> findAllByDeletedFalseAndAuthorCredentialsUsernameInOrderByPostedDesc(Set<String> usernames);

}
