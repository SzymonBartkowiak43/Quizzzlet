package com.example.quizlecikprojekt.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
