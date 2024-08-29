package com.example.quizlecikprojekt.domain;

import com.example.quizlecikprojekt.domain.rating.Rating;
import com.example.quizlecikprojekt.domain.rating.RatingRepository;
import com.example.quizlecikprojekt.domain.rating.RatingService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class RatingServiceTest {

    @Mock
    private  RatingRepository ratingRepository;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  VideoRepository videoRepository;

    @InjectMocks
    private RatingService ratingService;

    Video video = new Video();
    User user = new User();
    List<Rating> ratings = new ArrayList<>();


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        video.setId(1L);
        video.setTitle("videoWithoutRating");
        video.setUrl("url1");
        video.setUserId(1L);

        user.setEmail("test@wp.pl");
        user.setId(1L);
        user.setUserName("user");
        user.setPassword("password");

        Rating rating1 = new Rating();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setVideo(video);
        rating1.setDateAndTime(LocalDateTime.now());
        rating1.setUser(user);

        Rating rating2 = new Rating();
        rating2.setId(2L);
        rating2.setRating(3);
        rating2.setVideo(video);
        rating2.setDateAndTime(LocalDateTime.now());
        rating2.setUser(new User());

        Rating rating3 = new Rating();
        rating3.setId(3L);
        rating3.setRating(2);
        rating3.setVideo(video);
        rating3.setDateAndTime(LocalDateTime.now().minusDays(10));
        rating3.setUser(new User());


        ratings.add(rating1);
        ratings.add(rating2);
        ratings.add(rating3);

    }

    @Test
    public void getAverageRatingForVideoInLast7DaysTest() {
        // Given
        when(ratingRepository.findByVideoId(1L)).thenReturn(ratings);

        // When
        double averageRating = ratingService.getAverageRatingForVideoInLast7Days(1L);

        // Then
        assertEquals(4.0, averageRating, 0.01);
        verify(ratingRepository, times(1)).findByVideoId(1L);
    }

    @Test
    public void getAverageRatingForVideoInLast7DaysWithNoRatingsInLast7Days() {
        // Given
        Rating rating = new Rating();
        rating.setId(3L);
        rating.setRating(2);
        rating.setVideo(video);
        rating.setDateAndTime(LocalDateTime.now().minusDays(10));
        rating.setUser(new User());

        List<Rating> ratingList = new ArrayList<>();
        ratingList.add(rating);

        when(ratingRepository.findByVideoId(1L)).thenReturn(ratingList);
        // When
        double averageRating = ratingService.getAverageRatingForVideoInLast7Days(1L);
        // Then
        assertEquals(0.0, averageRating);
    }

    @Test
    public void getAverageRatingForVideoTest() {
        // Given
        when(ratingRepository.findByVideoId(1L)).thenReturn(ratings);

        // When
        double averageRating = ratingService.getAverageRatingForVideo(1L);

        // Then
        assertEquals(3.33, averageRating, 0.01);
        verify(ratingRepository, times(1)).findByVideoId(1L);

    }

    @Test
    public void getUserRatingForVideoUserHasRatingTest() {
        // Given
        String userEmail = "test@wp.pl";
        long videoId = 1L;
        Rating rating = ratings.get(0);

        when(ratingRepository.findByUserEmailAndVideoId(userEmail, videoId)).thenReturn(Optional.of(rating));
        // When
        Optional<Integer> result = ratingService.getUserRatingForVideo(userEmail, videoId);
        // Then
        assertTrue(result.isPresent());
        assertEquals(5, result.get().intValue());
        verify(ratingRepository, times(1)).findByUserEmailAndVideoId(userEmail, videoId);
    }

    @Test
    public void addOrUpdateRatingExistingRating() {
        // Given
        when(ratingRepository.findByUserEmailAndVideoId(user.getEmail(), video.getId()))
                .thenReturn(Optional.of(ratings.get(0)));

        // When
        ratingService.addOrUpdateRating(user.getEmail(), video.getId(), 1);

        // Then
        verify(ratingRepository, times(1)).save(ratings.get(0));
        assertEquals(1, ratings.get(0).getRating());
    }

    @Test
    public void addOrUpdateRatingUserNotFound() {
        // Given
        when(ratingRepository.findByUserEmailAndVideoId(user.getEmail(), video.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When
        ratingService.addOrUpdateRating(user.getEmail(), video.getId(), 1);

        // Then
        verify(ratingRepository, times(0)).save(any(Rating.class));

    }

    @Test
    public void addOrUpdateRatingVideoNotFound() {
        when(ratingRepository.findByUserEmailAndVideoId(user.getEmail(), video.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(videoRepository.findById(video.getId())).thenReturn(Optional.empty());

        // When
        ratingService.addOrUpdateRating(user.getEmail(), video.getId(), 1);

        // Then
        verify(ratingRepository, times(0)).save(any(Rating.class));
    }



}
