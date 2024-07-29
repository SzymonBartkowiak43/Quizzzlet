package com.example.quizlecikprojekt.word;

import com.example.quizlecikprojekt.wordSet.WordSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {
    List<Word> findByWordSet(Optional<WordSet> wordSet);

//    @Query("SELECT w FROM Word w " +
//            "INNER JOIN w.wordSet ws " +
//            "WHERE w.points <= 1 AND w.lastPracticed IS NOT NULL AND ws.user.id = 1 " +
//            "GROUP BY w.id " +
//            "HAVING COUNT(w.id) <= 5")
    @Query("SELECT w FROM Word w WHERE w.points = 0")
    List<Word> findWordsIdToRepeat();
}
