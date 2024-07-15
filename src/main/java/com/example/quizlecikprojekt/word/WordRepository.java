package com.example.quizlecikprojekt.word;

import com.example.quizlecikprojekt.wordSet.WordSet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {
    List<Word> findByWordSet(Optional<WordSet> wordSet);
}
