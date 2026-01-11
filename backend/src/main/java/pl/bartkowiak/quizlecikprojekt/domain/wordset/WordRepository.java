package pl.bartkowiak.quizlecikprojekt.domain.wordset;

import pl.bartkowiak.quizlecikprojekt.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WordRepository extends JpaRepository<Word, Long> {
}
