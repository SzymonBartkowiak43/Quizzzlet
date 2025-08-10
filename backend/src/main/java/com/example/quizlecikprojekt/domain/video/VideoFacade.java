package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.controllers.dto.video.*;
import com.example.quizlecikprojekt.domain.rating.RatingService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class VideoFacade {

  private static final Logger logger = LoggerFactory.getLogger(VideoFacade.class);

  private final VideoService videoService;
  private final UserService userService;
  private final RatingService ratingService;

  public VideoFacade(
      VideoService videoService, UserService userService, RatingService ratingService) {
    this.videoService = videoService;
    this.userService = userService;
    this.ratingService = ratingService;
  }

  public VideoDetailsResponse getVideoDetails(String userEmail, Long videoId) {
    logger.info("Getting video details for video: {} by user: {}", videoId, userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);
    User videoOwner = userService.getUserById(video.getUserId());

    Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, videoId);
    double averageRating = ratingService.getAverageRatingForVideo(videoId);

    return new VideoDetailsResponse(
        video.getId(),
        video.getTitle(),
        video.getUrl(),
        videoOwner.getName(),
        video.getUserId(),
        List.of(), // Comments will be handled by CommentFacade
        userRating.orElse(0),
        averageRating,
        0 // Comment count will be handled by CommentFacade
        );
  }

  public VideosListResponse getAllVideos(String userEmail) {
    logger.info("Getting all videos for user: {}", userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    List<Video> allVideos = videoService.findAll();
    List<Video> topRatedVideos = videoService.findTop4BestRatedVideosLast7Days();

    Map<Long, Double> videoRatings = buildVideoRatingsMap(allVideos);

    return new VideosListResponse(
        allVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
        topRatedVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
        allVideos.size());
  }

  public VideosListResponse searchVideos(String userEmail, String query) {
    logger.info("Searching videos for user: {} with query: '{}'", userEmail, query);

    if (query == null || query.trim().isEmpty()) {
      throw new IllegalArgumentException("Search query cannot be empty");
    }

    User currentUser = userService.getUserByEmail(userEmail);
    List<Video> searchResults = videoService.searchVideosByTitle(query.trim());
    Map<Long, Double> videoRatings = buildVideoRatingsMap(searchResults);

    return new VideosListResponse(
        searchResults.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
        List.of(), // Empty list for search results
        searchResults.size());
  }

  public VideoSummaryResponse addVideo(String userEmail, AddVideoRequest request) {
    logger.info("Adding video by user: {} - title: '{}'", userEmail, request.title());

    User currentUser = userService.getUserByEmail(userEmail);
    Video createdVideo = videoService.addVideo(request.url(), request.title(), currentUser.getId());

    return mapToVideoSummary(createdVideo, Map.of(createdVideo.getId(), 0.0));
  }

  public void deleteVideo(String userEmail, Long videoId) {
    logger.info("Deleting video {} by user: {}", videoId, userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);

    validateVideoDeletePermission(currentUser, video);

    videoService.deleteVideo(videoId);
    logger.info("Video {} deleted successfully by user: {}", videoId, userEmail);
  }

  public RatingResponse rateVideo(String userEmail, Long videoId, RateVideoRequest request) {
    logger.info("Rating video {} with {} by user: {}", videoId, request.rating(), userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);

    ratingService.addOrUpdateRating(userEmail, videoId, request.rating());
    double newAverageRating = ratingService.getAverageRatingForVideo(videoId);

    return new RatingResponse(request.rating(), newAverageRating);
  }

  public RatingResponse getVideoRating(String userEmail, Long videoId) {
    logger.info("Getting rating for video {} by user: {}", videoId, userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);

    Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, videoId);
    double averageRating = ratingService.getAverageRatingForVideo(videoId);

    return new RatingResponse(userRating.orElse(0), averageRating);
  }

  // Helper methods
  private Video findVideoById(Long videoId) {
    Video video = videoService.findById(videoId);
    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + videoId);
    }
    return video;
  }

  private Map<Long, Double> buildVideoRatingsMap(List<Video> videos) {
    return videos.stream()
        .collect(
            Collectors.toMap(
                Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));
  }

  private VideoSummaryResponse mapToVideoSummary(Video video, Map<Long, Double> videoRatings) {
    try {
      User owner = userService.getUserById(video.getUserId());
      return new VideoSummaryResponse(
          video.getId(),
          video.getTitle(),
          video.getUrl(),
          owner.getName(),
          video.getUserId(),
          videoRatings.getOrDefault(video.getId(), 0.0));
    } catch (Exception e) {
      logger.warn("Could not find owner for video {}", video.getId());
      return new VideoSummaryResponse(
          video.getId(),
          video.getTitle(),
          video.getUrl(),
          "Unknown",
          video.getUserId(),
          videoRatings.getOrDefault(video.getId(), 0.0));
    }
  }

  private void validateVideoDeletePermission(User currentUser, Video video) {
    boolean isAdmin =
        currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

    if (!video.getUserId().equals(currentUser.getId()) && !isAdmin) {
      throw new AccessDeniedException("You don't have permission to delete this video");
    }
  }
}
