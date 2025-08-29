package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.video.*;
import com.example.quizlecikprojekt.domain.comment.CommentFacade;
import com.example.quizlecikprojekt.domain.video.VideoFacade;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

  private final VideoFacade videoFacade;
  private final CommentFacade commentFacade;

  public VideoController(VideoFacade videoFacade, CommentFacade commentFacade) {
    this.videoFacade = videoFacade;
    this.commentFacade = commentFacade;
  }

  @GetMapping("/{id}")
  public ResponseEntity<VideoDetailsResponse> getVideoDetails(
      @PathVariable Long id, Authentication authentication) {

    VideoDetailsResponse videoDetails = videoFacade.getVideoDetails(authentication.getName(), id);
    List<CommentResponse> comments = commentFacade.getVideoCommentsForDetails(id);

    VideoDetailsResponse response =
        new VideoDetailsResponse(
            videoDetails.id(),
            videoDetails.title(),
            videoDetails.url(),
            videoDetails.ownerName(),
            videoDetails.ownerId(),
            comments,
            videoDetails.userRating(),
            videoDetails.averageRating(),
            comments.size());

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<VideosListResponse> getAllVideos(Authentication authentication) {
    VideosListResponse response = videoFacade.getAllVideos(authentication.getName());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<VideosListResponse> searchVideos(
      @RequestParam String query, Authentication authentication) {

    VideosListResponse response = videoFacade.searchVideos(authentication.getName(), query);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<VideoSummaryResponse> addVideo(
      @Valid @RequestBody AddVideoRequest request, Authentication authentication) {

    VideoSummaryResponse response = videoFacade.addVideo(authentication.getName(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVideo(@PathVariable Long id, Authentication authentication) {

    videoFacade.deleteVideo(authentication.getName(), id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{videoId}/rating")
  public ResponseEntity<RatingResponse> rateVideo(
      @PathVariable Long videoId,
      @Valid @RequestBody RateVideoRequest request,
      Authentication authentication) {

    RatingResponse response = videoFacade.rateVideo(authentication.getName(), videoId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{videoId}/rating")
  public ResponseEntity<RatingResponse> getVideoRating(
      @PathVariable Long videoId, Authentication authentication) {

    RatingResponse response = videoFacade.getVideoRating(authentication.getName(), videoId);
    return ResponseEntity.ok(response);
  }
}
