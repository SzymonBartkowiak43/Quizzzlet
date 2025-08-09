package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.comment.CommentService;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
import com.example.quizlecikprojekt.domain.rating.RatingService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoService;
import com.example.quizlecikprojekt.controllers.dto.video.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoRestController {

  private static final Logger logger = LoggerFactory.getLogger(VideoRestController.class);

  private final CommentService commentService;
  private final VideoService videoService;
  private final UserService userService;
  private final RatingService ratingService;

  public VideoRestController(
      CommentService commentService,
      VideoService videoService,
      UserService userService,
      RatingService ratingService) {
    this.commentService = commentService;
    this.videoService = videoService;
    this.userService = userService;
    this.ratingService = ratingService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<VideoDetailsResponse> getVideoDetails(
      @PathVariable Long id, Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video video = videoService.findById(id);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + id);
    }

    User videoOwner = userService.getUserById(video.getUserId());
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(video.getId());
    Optional<Integer> userRating = ratingService.getUserRatingForVideo(currentUser.getEmail(), id);
    double averageRating = ratingService.getAverageRatingForVideo(id);

    VideoDetailsResponse response =
        new VideoDetailsResponse(
            video.getId(),
            video.getTitle(),
            video.getUrl(),
            videoOwner.getName(),
            video.getUserId(),
            comments.stream().map(this::mapToCommentResponse).toList(),
            userRating.orElse(0),
            averageRating,
            comments.size());

    logger.info("Video details retrieved for video: {} by user: {}", id, currentUser.getEmail());
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<VideosListResponse> getAllVideos(Authentication authentication) {
    User currentUser = userService.getUserByEmail(authentication.getName());

    List<Video> allVideos = videoService.findAll();
    List<Video> topRatedVideos = videoService.findTop4BestRatedVideosLast7Days();

    Map<Long, Double> videoRatings =
        allVideos.stream()
            .collect(
                Collectors.toMap(
                    Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));

    VideosListResponse response =
        new VideosListResponse(
            allVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
            topRatedVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
            allVideos.size());

    logger.info("Videos list retrieved by user: {}", currentUser.getEmail());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<VideosListResponse> searchVideos(
      @RequestParam String query, Authentication authentication) {

    if (query == null || query.trim().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    User currentUser = userService.getUserByEmail(authentication.getName());
    List<Video> searchResults = videoService.searchVideosByTitle(query.trim());

    Map<Long, Double> videoRatings =
        searchResults.stream()
            .collect(
                Collectors.toMap(
                    Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));

    VideosListResponse response =
        new VideosListResponse(
            searchResults.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList(),
            List.of(), // Empty list for search results
            searchResults.size());

    logger.info(
        "Video search performed by user: {} for query: '{}'", currentUser.getEmail(), query);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<VideoSummaryResponse> addVideo(
      @Valid @RequestBody AddVideoRequest request, Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video createdVideo = videoService.addVideo(request.url(), request.title(), currentUser.getId());

    VideoSummaryResponse response =
        mapToVideoSummary(createdVideo, Map.of(createdVideo.getId(), 0.0));

    logger.info("Video added by user: {} - title: '{}'", currentUser.getEmail(), request.title());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVideo(@PathVariable Long id, Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video video = videoService.findById(id);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + id);
    }

    // Check if user owns the video or is admin
    boolean isAdmin =
        currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

    if (!video.getUserId().equals(currentUser.getId()) && !isAdmin) {
      throw new AccessDeniedException("You don't have permission to delete this video");
    }

    videoService.deleteVideo(id);
    logger.info("Video {} deleted by user: {}", id, currentUser.getEmail());

    return ResponseEntity.noContent().build();
  }

  // Comment endpoints
  @PostMapping("/{id}/comments")
  public ResponseEntity<CommentResponse> addComment(
      @PathVariable Long id,
      @Valid @RequestBody AddCommentRequest request,
      Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video video = videoService.findById(id);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + id);
    }

    Comment comment = commentService.addComment(request.content(), currentUser, video);
    CommentResponse response = mapToCommentResponse(comment);

    logger.info("Comment added by user: {} to video: {}", currentUser.getEmail(), id);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}/comments")
  public ResponseEntity<List<CommentResponse>> getVideoComments(@PathVariable Long id) {
    Video video = videoService.findById(id);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + id);
    }

    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(id);
    List<CommentResponse> response = comments.stream().map(this::mapToCommentResponse).toList();

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{videoId}/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable Long videoId, @PathVariable Long commentId, Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Comment comment = commentService.findById(commentId);

    if (comment == null) {
      throw new EntityNotFoundException("Comment not found with id: " + commentId);
    }

    // Check if user owns the comment or is admin
    boolean isAdmin =
        currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

    if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
      throw new AccessDeniedException("You can only delete your own comments");
    }

    commentService.deleteComment(commentId);
    logger.info(
        "Comment {} deleted from video {} by user: {}", commentId, videoId, currentUser.getEmail());

    return ResponseEntity.noContent().build();
  }

  // Rating endpoints
  @PostMapping("/{videoId}/rating")
  public ResponseEntity<RatingResponse> rateVideo(
      @PathVariable Long videoId,
      @Valid @RequestBody RateVideoRequest request,
      Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video video = videoService.findById(videoId);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + videoId);
    }

    ratingService.addOrUpdateRating(currentUser.getEmail(), videoId, request.rating());
    double newAverageRating = ratingService.getAverageRatingForVideo(videoId);

    RatingResponse response = new RatingResponse(request.rating(), newAverageRating);

    logger.info("Video {} rated {} by user: {}", videoId, request.rating(), currentUser.getEmail());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{videoId}/rating")
  public ResponseEntity<RatingResponse> getVideoRating(
      @PathVariable Long videoId, Authentication authentication) {

    User currentUser = userService.getUserByEmail(authentication.getName());
    Video video = videoService.findById(videoId);

    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + videoId);
    }

    Optional<Integer> userRating =
        ratingService.getUserRatingForVideo(currentUser.getEmail(), videoId);
    double averageRating = ratingService.getAverageRatingForVideo(videoId);

    RatingResponse response = new RatingResponse(userRating.orElse(0), averageRating);
    return ResponseEntity.ok(response);
  }

  // Helper methods
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

  private CommentResponse mapToCommentResponse(CommentDto commentDto) {
    return new CommentResponse(
        commentDto.id(),
        commentDto.content(),
        commentDto.user().getName(),
        commentDto.dateAndTime());
  }

  private CommentResponse mapToCommentResponse(Comment comment) {
    return new CommentResponse(
        comment.getId(), comment.getContent(), comment.getUser().getName(), comment.getCreatedAt());
  }
}
