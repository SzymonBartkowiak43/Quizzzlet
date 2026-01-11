package pl.bartkowiak.quizlecikprojekt.domain.wordset;

import pl.bartkowiak.quizlecikprojekt.entity.User;
import java.util.List;

import pl.bartkowiak.quizlecikprojekt.entity.WordSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface WordSetRepository extends CrudRepository<WordSet, Long> {
  @Query(
      "SELECT ws FROM WordSet ws LEFT JOIN FETCH ws.words WHERE ws.user = :user ORDER BY ws.createdAt DESC")
  List<WordSet> findByUserWithWordsOrderByCreatedAtDesc(@Param("user") User user);
}
