package com.example.quizlecikprojekt.domain.wordset;

import com.example.quizlecikprojekt.entity.WordSet;
import java.util.List;
import java.util.Optional;

import com.example.quizlecikprojekt.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WordRepository extends JpaRepository<Word, Long> {
}
