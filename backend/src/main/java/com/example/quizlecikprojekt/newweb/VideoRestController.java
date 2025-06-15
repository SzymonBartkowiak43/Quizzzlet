package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.comment.CommentService;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
import com.example.quizlecikprojekt.domain.rating.RatingService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoService;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.video.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoRestController {

    private static final Logger logger = LoggerFactory.getLogger(VideoRestController.class);

    private final CommentService commentService;
    private final VideoService videoService;
    private final UserService userService;
    private final RatingService ratingService;

    public VideoRestController(CommentService commentService, VideoService videoService,
                               UserService userService, RatingService ratingService) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoDetailsResponse>> getVideoDetails(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            Video video = videoService.findById(id);

            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Video not found"));
            }

            User videoOwner = userService.getUserById(video.getUserId());
            List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(video.getId());
            Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, id);
            double averageRating = ratingService.getAverageRatingForVideo(id);

            VideoDetailsResponse response = new VideoDetailsResponse(
                    video.getId(),
                    video.getTitle(),
                    video.getUrl(),
                    videoOwner.getUserName(),
                    video.getUserId(),
                    comments.stream().map(this::mapToCommentResponse).toList(),
                    userRating.orElse(0),
                    averageRating,
                    comments.size());

            logger.info("Video details retrieved for video: {} by user: {}", id, userEmail);
            return ResponseEntity.ok(ApiResponse.success("Video details retrieved", response));

        } catch (Exception e) {
            logger.error("Error retrieving video details for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve video details"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<VideosListResponse>> getAllVideos(
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            List<Video> allVideos = videoService.findAll();
            List<Video> topRatedVideos = videoService.findTop4BestRatedVideosLast7Days();

            Map<Long, Double> videoRatings = allVideos.stream()
                    .collect(Collectors.toMap(Video::getId,
                            video -> ratingService.getAverageRatingForVideo(video.getId())));

            VideosListResponse response = new VideosListResponse();
            response.setVideos(allVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList());
            response.setTopRatedVideos(topRatedVideos.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList());
            response.setTotalVideos(allVideos.size());

            logger.info("Videos list retrieved by user: {}", authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Videos retrieved successfully", response));

        } catch (Exception e) {
            logger.error("Error retrieving videos list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve videos"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<VideosListResponse>> searchVideos(
            @RequestParam String query,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Search query cannot be empty"));
            }

            List<Video> searchResults = videoService.searchVideosByTitle(query.trim());
            Map<Long, Double> videoRatings = searchResults.stream()
                    .collect(Collectors.toMap(Video::getId,
                            video -> ratingService.getAverageRatingForVideo(video.getId())));

            VideosListResponse response = new VideosListResponse();
            response.setVideos(searchResults.stream().map(video -> mapToVideoSummary(video, videoRatings)).toList());
            response.setTopRatedVideos(List.of()); // Pusta lista dla wyszukiwania
            response.setTotalVideos(searchResults.size());

            logger.info("Video search performed by user: {} for query: '{}'", authentication.getName(), query);
            return ResponseEntity.ok(ApiResponse.success("Search completed", response));

        } catch (Exception e) {
            logger.error("Error searching videos for query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search videos"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VideoSummaryResponse>> addVideo(
            @Valid @RequestBody AddVideoRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            User user = userService.getUserByEmail(userEmail);

            Video createdVideo = videoService.addVideo(request.url(), request.title(), user.getId());

            VideoSummaryResponse response = mapToVideoSummary(createdVideo, Map.of(createdVideo.getId(), 0.0));

            logger.info("Video added by user: {} - title: '{}'", userEmail, request.title());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Video added successfully", response));

        } catch (Exception e) {
            logger.error("Error adding video for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add video"));
        }
    }

    // === COMMENTS ENDPOINTS ===

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            User user = userService.getUserByEmail(userEmail);
            Video video = videoService.findById(id);

            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Video not found"));
            }

            Comment comment = commentService.addComment(request.content(), user, video);
            CommentResponse response = mapToCommentResponse(comment);

            logger.info("Comment added by user: {} to video: {}", userEmail, id);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Comment added successfully", response));

        } catch (Exception e) {
            logger.error("Error adding comment to video: {} by user: {}", id,
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add comment"));
        }
    }

    @DeleteMapping("/{videoId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable Long videoId,
            @PathVariable Long commentId,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            Comment comment = commentService.findById(commentId);

            if (comment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Comment not found"));
            }

            User currentUser = userService.getUserByEmail(userEmail);
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));

            if (!isAdmin && !comment.getUser().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied - you can only delete your own comments"));
            }

            commentService.deleteComment(commentId);

            logger.info("Comment {} deleted from video {} by user: {}", commentId, videoId, userEmail);
            return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", "Deleted"));

        } catch (Exception e) {
            logger.error("Error deleting comment {} from video {} by user: {}",
                    commentId, videoId,
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete comment"));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getVideoComments(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            Video video = videoService.findById(id);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Video not found"));
            }

            List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(id);
            List<CommentResponse> response = comments.stream()
                    .map(this::mapToCommentResponse)
                    .toList();

            return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", response));

        } catch (Exception e) {
            logger.error("Error retrieving comments for video: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve comments"));
        }
    }


    @PostMapping("/{videoId}/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> rateVideo(
            @PathVariable Long videoId,
            @Valid @RequestBody RateVideoRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            Video video = videoService.findById(videoId);

            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Video not found"));
            }

            ratingService.addOrUpdateRating(userEmail, videoId, request.rating());
            double newAverageRating = ratingService.getAverageRatingForVideo(videoId);

            RatingResponse response = new RatingResponse(
                    request.rating(),
                    newAverageRating);

            logger.info("Video {} rated {} by user: {}", videoId, request.rating(), userEmail);
            return ResponseEntity.ok(ApiResponse.success("Rating submitted successfully", response));

        } catch (Exception e) {
            logger.error("Error rating video {} by user: {}", videoId,
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to submit rating"));
        }
    }

    @GetMapping("/{videoId}/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> getVideoRating(
            @PathVariable Long videoId,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();
            Video video = videoService.findById(videoId);

            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Video not found"));
            }

            Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, videoId);
            double averageRating = ratingService.getAverageRatingForVideo(videoId);

            RatingResponse response = new RatingResponse(userRating.orElse(0), averageRating);

            return ResponseEntity.ok(ApiResponse.success("Rating retrieved successfully", response));

        } catch (Exception e) {
            logger.error("Error retrieving rating for video: {}", videoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve rating"));
        }
    }


    private VideoSummaryResponse mapToVideoSummary(Video video, Map<Long, Double> videoRatings) {
        VideoSummaryResponse response = new VideoSummaryResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setUrl(video.getUrl());
        response.setOwnerId(video.getUserId());
        response.setAverageRating(videoRatings.getOrDefault(video.getId(), 0.0));

        try {
            User owner = userService.getUserById(video.getUserId());
            response.setOwnerUsername(owner.getUserName());
        } catch (Exception e) {
            response.setOwnerUsername("Unknown");
        }

        return response;
    }

    private CommentResponse mapToCommentResponse(CommentDto commentDto) {
        return new CommentResponse(
                commentDto.id(),
                commentDto.content(),
                commentDto.user().getUserName(),
                commentDto.dateAndTime());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getUser().getUserName(), comment.getCreatedAt());
    }
}