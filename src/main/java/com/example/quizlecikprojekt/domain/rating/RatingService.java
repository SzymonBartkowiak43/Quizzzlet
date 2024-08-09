package com.example.quizlecikprojekt.domain.rating;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public void addOrUpdateRating(String userEmail, long videoId, int rating) {
        Rating ratingToSaveOrUpdate = ratingRepository.findByUser_EmailAndVideo_Id(userEmail, videoId)
                .orElseGet(Rating::new);
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();
        ratingToSaveOrUpdate.setUser(user);
        ratingToSaveOrUpdate.setVideo(video);
        ratingToSaveOrUpdate.setRating(rating);
        ratingToSaveOrUpdate.setDateAndTime(LocalDateTime.now());
        ratingRepository.save(ratingToSaveOrUpdate);
    }

    public Optional<Integer> getUserRatingForVideo(String userEmail, long videoId) {
        return ratingRepository.findByUser_EmailAndVideo_Id(userEmail, videoId)
                .map(Rating::getRating);
    }

    public double getAverageRatingForVideo(long videoId) {
        List<Rating> ratings = ratingRepository.findByVideo_Id(videoId);
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
    }
}