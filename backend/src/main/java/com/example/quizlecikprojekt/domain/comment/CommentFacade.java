package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.controllers.dto.video.AddCommentRequest;
import com.example.quizlecikprojekt.controllers.dto.video.CommentResponse;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {

  private static final Logger logger = LoggerFactory.getLogger(CommentFacade.class);

  private final CommentService commentService;
  private final UserService userService;
  private final VideoService videoService;

  public CommentFacade(
      CommentService commentService, UserService userService, VideoService videoService) {
    this.commentService = commentService;
    this.userService = userService;
    this.videoService = videoService;
  }

  public CommentResponse addComment(String userEmail, Long videoId, AddCommentRequest request) {
    logger.info("Adding comment by user: {} to video: {}", userEmail, videoId);

    User currentUser = userService.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);

    Comment comment = commentService.addComment(request.content(), currentUser, video);
    return mapToCommentResponse(comment);
  }

  public List<CommentResponse> getVideoComments(Long videoId) {
    logger.info("Getting comments for video: {}", videoId);

    Video video = findVideoById(videoId);
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(videoId);

    return comments.stream().map(this::mapToCommentResponse).toList();
  }

  public void deleteComment(String userEmail, Long videoId, Long commentId) {
    logger.info("Deleting comment {} from video {} by user: {}", commentId, videoId, userEmail);

    User currentUser = userService.getUserByEmail(userEmail);
    Comment comment = findCommentById(commentId);

    validateCommentDeletePermission(currentUser, comment);

    commentService.deleteComment(commentId);
    logger.info("Comment {} deleted successfully by user: {}", commentId, userEmail);
  }

  public int getVideoCommentsCount(Long videoId) {
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(videoId);
    return comments.size();
  }

  public List<CommentResponse> getVideoCommentsForDetails(Long videoId) {
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(videoId);
    return comments.stream().map(this::mapToCommentResponse).toList();
  }

  // Helper methods
  private Video findVideoById(Long videoId) {
    Video video = videoService.findById(videoId);
    if (video == null) {
      throw new EntityNotFoundException("Video not found with id: " + videoId);
    }
    return video;
  }

  private Comment findCommentById(Long commentId) {
    Comment comment = commentService.findById(commentId);
    if (comment == null) {
      throw new EntityNotFoundException("Comment not found with id: " + commentId);
    }
    return comment;
  }

  private void validateCommentDeletePermission(User currentUser, Comment comment) {
    boolean isAdmin =
        currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

    if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
      throw new AccessDeniedException("You can only delete your own comments");
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
