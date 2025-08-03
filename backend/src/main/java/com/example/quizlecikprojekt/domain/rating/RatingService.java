package com.example.quizlecikprojekt.domain.rating;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
  private final RatingRepository ratingRepository;
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;

  public RatingService(
      RatingRepository ratingRepository,
      UserRepository userRepository,
      VideoRepository videoRepository) {
    this.ratingRepository = ratingRepository;
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
  }

  public void addOrUpdateRating(String userEmail, long videoId, int rating) {
    Rating ratingToSave =
        ratingRepository.findByUserEmailAndVideoId(userEmail, videoId).orElseGet(Rating::new);

    if (ratingToSave.getUser() == null || ratingToSave.getVideo() == null) {
      User user =
          userRepository
              .getUserByEmail(userEmail)
              .orElseThrow(
                  () -> new NoSuchElementException("User not found with email: " + userEmail));
      Video video =
          videoRepository
              .findById(videoId)
              .orElseThrow(() -> new NoSuchElementException("Video not found with id: " + videoId));
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
