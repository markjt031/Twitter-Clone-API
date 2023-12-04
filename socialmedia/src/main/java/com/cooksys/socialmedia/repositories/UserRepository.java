package com.cooksys.socialmedia.repositories;

import java.util.List;
import java.util.Optional;

import com.cooksys.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.entities.Credentials;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<List<User>> findAllByDeletedFalse();

  Optional<User> findByCredentialsUsername(String username);

  Optional<User> findByCredentials(Credentials credentials);

  Optional<User> findByCredentialsUsernameIgnoreCase(String username);

}