package com.example.quizlecikprojekt.domain.rating;

import com.example.quizlecikprojekt.domain.video.VideoFacade;
import com.example.quizlecikprojekt.entity.Video;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RatingFacade {

    private final RatingService ratingService;

    public double getAverageRatingForVideoInLast7Days(Long id) {
        return ratingService.getAverageRatingForVideoInLast7Days(id);
    }

    public Double getAverageRatingForVideo(Long id) {
        return ratingService.getAverageRatingForVideo(id);
    }

    public Optional<Integer> getUserRatingForVideo(String userEmail, Long videoId) {
        return ratingService.getUserRatingForVideo(userEmail, videoId);
    }

    public void addOrUpdateRating(String userEmail, Long videoId, Integer rating, Video video) {
        ratingService.addOrUpdateRating(userEmail, videoId, rating,video);
    }
}
