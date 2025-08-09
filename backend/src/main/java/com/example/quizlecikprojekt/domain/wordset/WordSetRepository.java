package com.example.quizlecikprojekt.domain.wordset;

import com.example.quizlecikprojekt.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WordSetRepository extends CrudRepository<WordSet, Long> {
  List<WordSet> findByUser(Optional<User> user);

  @Query(
      "SELECT ws FROM WordSet ws LEFT JOIN FETCH ws.words WHERE ws.user = :user ORDER BY ws.createdAt DESC")
  List<WordSet> findByUserWithWordsOrderByCreatedAtDesc(@Param("user") User user);
}
