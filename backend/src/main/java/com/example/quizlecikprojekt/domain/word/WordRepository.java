package com.example.quizlecikprojekt.domain.word;

import com.example.quizlecikprojekt.domain.wordset.WordSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
  List<Word> findByWordSet(Optional<WordSet> wordSet);

}
