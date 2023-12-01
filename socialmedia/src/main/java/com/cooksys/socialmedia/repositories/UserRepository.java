package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<List<User>> findAllByDeletedFalse();

  Optional<User> findByCredentialsUsername(String username);

  Optional<User> findByCredentialsUsernameIgnoreCase(String username);

}
