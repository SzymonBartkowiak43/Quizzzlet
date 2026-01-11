package pl.bartkowiak.quizlecikprojekt.domain.comment;

import pl.bartkowiak.quizlecikprojekt.controllers.dto.video.AddCommentRequest;
import pl.bartkowiak.quizlecikprojekt.controllers.dto.video.CommentResponse;
import pl.bartkowiak.quizlecikprojekt.domain.comment.dto.CommentDto;
import pl.bartkowiak.quizlecikprojekt.domain.user.UserFacade;
import pl.bartkowiak.quizlecikprojekt.domain.video.VideoFacade;
import pl.bartkowiak.quizlecikprojekt.entity.User;
import pl.bartkowiak.quizlecikprojekt.entity.Video;
import pl.bartkowiak.quizlecikprojekt.entity.Comment;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentFacade {

  private final CommentService commentService;
  private final UserFacade userFacade;
  private final VideoFacade videoFacade;



  public CommentResponse addComment(String userEmail, Long videoId, AddCommentRequest request) {
    User currentUser = userFacade.getUserByEmail(userEmail);
    Video video = findVideoById(videoId);

    Comment comment = commentService.addComment(request.content(), currentUser, video);
    return mapToCommentResponse(comment);
  }

  public List<CommentResponse> getVideoComments(Long videoId) {
    Video video = findVideoById(videoId);
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(videoId);

    return comments.stream().map(this::mapToCommentResponse).toList();
  }

  public void deleteComment(String userEmail, Long videoId, Long commentId) {
    User currentUser = userFacade.getUserByEmail(userEmail);
    Comment comment = findCommentById(commentId);

    validateCommentDeletePermission(currentUser, comment);

    commentService.deleteComment(commentId);
  }

  public List<CommentResponse> getVideoCommentsForDetails(Long videoId) {
    List<CommentDto> comments = commentService.findAllDtoCommentsByVideoId(videoId);
    return comments.stream().map(this::mapToCommentResponse).toList();
  }

  private Video findVideoById(Long videoId) {
    Video video = videoFacade.findById(videoId);
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
