package com.example.quizlecikprojekt.domain.rating;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(RatingService .class);

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public void addOrUpdateRating(String userEmail, long videoId, int rating) {
        LOGGER.info("Entering addOrUpdateRating with userEmail: {}, videoId: {}, rating: {}", userEmail, videoId, rating);
        try {
            Rating ratingToSave = ratingRepository.findByUserEmailAndVideoId(userEmail, videoId)
                    .orElseGet(Rating::new);

            if (ratingToSave.getUser() == null || ratingToSave.getVideo() == null) {
                User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                        new NoSuchElementException("User not found with email: " + userEmail));
                Video video = videoRepository.findById(videoId).orElseThrow(() ->
                        new NoSuchElementException("Video not found with id: " + videoId));
                ratingToSave.setUser(user);
                ratingToSave.setVideo(video);
            }

            ratingToSave.setRating(rating);
            ratingToSave.setDateAndTime(LocalDateTime.now());

            ratingRepository.save(ratingToSave);
            LOGGER.info("Rating successfully saved for userEmail: {}, videoId: {}", userEmail, videoId);
        } catch (NoSuchElementException e) {
            LOGGER.error("Entity not found: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in addOrUpdateRating: {}", e.getMessage());
        }
    }

    public Optional<Integer> getUserRatingForVideo(String userEmail, long videoId) {
        LOGGER.info("Entering getUserRatingForVideo with userEmail: {}, videoId: {}", userEmail, videoId);
        return ratingRepository.findByUserEmailAndVideoId(userEmail, videoId)
                .map(Rating::getRating);
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





















