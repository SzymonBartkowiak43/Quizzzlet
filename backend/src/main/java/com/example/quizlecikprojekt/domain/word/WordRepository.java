package com.example.quizlecikprojekt.domain.word;

import com.example.quizlecikprojekt.domain.wordset.WordSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {
  List<Word> findByWordSet(Optional<WordSet> wordSet);

  @Query(
      "SELECT w FROM Word w "
          + "INNER JOIN w.wordSet ws "
          + "WHERE w.points <= 1 AND w.lastPracticed IS NOT NULL AND ws.user.id = :userId ")
  List<Word> findWordsToRepeat(Long userId);
}
