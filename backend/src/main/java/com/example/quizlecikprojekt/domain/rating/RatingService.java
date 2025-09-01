package com.example.quizlecikprojekt.domain.rating;

import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.domain.video.VideoFacade;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Video;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.example.quizlecikprojekt.entity.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class RatingService {

  private final RatingRepository ratingRepository;
  private final UserFacade userFacade;

  public void addOrUpdateRating(String userEmail, long videoId, int rating, Video video) {
    Rating ratingToSave =
        ratingRepository.findByUserEmailAndVideoId(userEmail, videoId).orElseGet(Rating::new);

    if (ratingToSave.getUser() == null || ratingToSave.getVideo() == null) {
      User user = userFacade.getUserByEmail(userEmail);
      ratingToSave.setUser(user);
      ratingToSave.setVideo(video);
    }

    ratingToSave.setRating(rating);
    ratingToSave.setDateAndTime(LocalDateTime.now());

    ratingRepository.save(ratingToSave);
  }

  public Optional<Integer> getUserRatingForVideo(String userEmail, long videoId) {
    return ratingRepository.findByUserEmailAndVideoId(userEmail, videoId).map(Rating::getRating);
  }

  public double getAverageRatingForVideo(long videoId) {
    List<Rating> ratings = ratingRepository.findByVideoId(videoId);
    return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
  }

  public double getAverageRatingForVideoInLast7Days(long videoId) {
    return ratingRepository.findByVideoId(videoId).stream()
        .filter(rating -> rating.getDateAndTime().isAfter(LocalDateTime.now().minusDays(7)))
        .mapToInt(Rating::getRating)
        .average()
        .orElse(0.0);
  }
}
