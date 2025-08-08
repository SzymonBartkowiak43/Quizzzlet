package com.example.quizlecikprojekt.domain.user;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
  User getUserById(Long id);

  Optional<User> getUserByEmail(String email);

  Optional<User> getUserByName(String name);
}
