package com.example.quizlecikprojekt.domain.wordset;

import com.example.quizlecikprojekt.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordSetRepository extends CrudRepository<WordSet, Long> {
    List<WordSet> findByUser(Optional<User> user);
}
