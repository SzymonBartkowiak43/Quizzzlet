package com.example.quizlecikprojekt.domain.rating;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends CrudRepository<Rating, Long> {
    Optional<Rating> findByUserEmailAndVideoId(String userEmail, Long VideoId);
    List<Rating> findByVideoId(Long videoId);
}
