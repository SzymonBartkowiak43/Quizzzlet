package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.controllers.dto.video.*;
import com.example.quizlecikprojekt.domain.rating.RatingFacade;
import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Video;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VideoFacade {

  private final VideoService videoService;
  private final UserFacade userFacade;
  private final RatingFacade ratingFacade;


  public VideoDetailsResponse getVideoDetails(String userEmail, Long videoId) {
    Video video = videoService.findById(videoId);
    User videoOwner = userFacade.getUserById(video.getUserId());

    Optional<Integer> userRating = ratingFacade.getUserRatingForVideo(userEmail, videoId);
    double averageRating = ratingFacade.getAverageRatingForVideo(videoId);

    return new VideoDetailsResponse(
        video.getId(),
        video.getTitle(),
        video.getUrl(),
        videoOwner.getName(),
        video.getUserId(),
        List.of(),
        userRating.orElse(0),
        averageRating,
        0
        );
  }

  public VideosListResponse getAllVideos(String userEmail) {
    List<Video> allVideos = videoService.findAll();
    List<Video> topRatedVideos = videoService.findTop4BestRatedVideosLast7Days();
    User owner = userFacade.getUserByEmail(userEmail);

    Map<Long, Double> videoRatings = videoService.buildVideoRatingsMap(allVideos);

    return new VideosListResponse(
        allVideos.stream().map(video -> videoService.mapToVideoSummary(video, videoRatings, owner)).toList(),
        topRatedVideos.stream().map(video -> videoService.mapToVideoSummary(video, videoRatings, owner)).toList(),
        allVideos.size());
  }

  public VideosListResponse searchVideos(String userEmail, String query) {
    if (query == null || query.trim().isEmpty()) {
      throw new IllegalArgumentException("Search query cannot be empty");
    }

    User owner = userFacade.getUserByEmail(userEmail);
    List<Video> searchResults = videoService.searchVideosByTitle(query.trim());
    Map<Long, Double> videoRatings = videoService.buildVideoRatingsMap(searchResults);

    return new VideosListResponse(
        searchResults.stream().map(video -> videoService.mapToVideoSummary(video, videoRatings,owner)).toList(),
        List.of(),
        searchResults.size());
  }

  public VideoSummaryResponse addVideo(String userEmail, AddVideoRequest request) {
    User currentUser = userFacade.getUserByEmail(userEmail);
    Video createdVideo = videoService.addVideo(request.url(), request.title(), currentUser.getId());

    return videoService.mapToVideoSummary(createdVideo, Map.of(createdVideo.getId(), 0.0), currentUser);
  }

  public void deleteVideo(String userEmail, Long videoId) {
    User currentUser = userFacade.getUserByEmail(userEmail);
    Video video = videoService.findById(videoId);

    validateVideoDeletePermission(currentUser, video);

    videoService.deleteVideo(videoId);
  }

  public RatingResponse rateVideo(String userEmail, Long videoId, RateVideoRequest request) {
    Video video = videoService.findById(videoId);
    ratingFacade.addOrUpdateRating(userEmail, videoId, request.rating(),video);
    double newAverageRating = ratingFacade.getAverageRatingForVideo(videoId);

    return new RatingResponse(request.rating(), newAverageRating);
  }

  public RatingResponse getVideoRating(String userEmail, Long videoId) {
    Optional<Integer> userRating = ratingFacade.getUserRatingForVideo(userEmail, videoId);
    double averageRating = ratingFacade.getAverageRatingForVideo(videoId);

    return new RatingResponse(userRating.orElse(0), averageRating);
  }



  private void validateVideoDeletePermission(User currentUser, Video video) {
    boolean isAdmin =
        currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

    if (!video.getUserId().equals(currentUser.getId()) && !isAdmin) {
      throw new AccessDeniedException("You don't have permission to delete this video");
    }
  }

  public Video findById(long videoId) {
    return videoService.findById(videoId);
  }
}
