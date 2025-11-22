package com.example.quizlecikprojekt.domain.user;

import java.util.List;
import java.util.Optional;

import com.example.quizlecikprojekt.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> getUserByEmail(String email);

  Optional<User> getUserByName(String name);

  @Override
  List<User> findAll();
}
