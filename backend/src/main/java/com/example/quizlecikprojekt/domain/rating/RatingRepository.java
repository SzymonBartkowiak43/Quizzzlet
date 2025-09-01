package com.example.quizlecikprojekt.domain.rating;

import java.util.List;
import java.util.Optional;

import com.example.quizlecikprojekt.entity.Rating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RatingRepository extends CrudRepository<Rating, Long> {
  Optional<Rating> findByUserEmailAndVideoId(String userEmail, Long videoId);

  List<Rating> findByVideoId(Long videoId);
}
