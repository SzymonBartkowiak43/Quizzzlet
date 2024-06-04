package com.example.quizlecikprojekt.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
}
