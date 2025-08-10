package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.video.AddCommentRequest;
import com.example.quizlecikprojekt.controllers.dto.video.CommentResponse;
import com.example.quizlecikprojekt.domain.comment.CommentFacade;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

  private final CommentFacade commentFacade;

  public CommentController(CommentFacade commentFacade) {
    this.commentFacade = commentFacade;
  }

  @PostMapping("/{id}/comments")
  public ResponseEntity<CommentResponse> addComment(
      @PathVariable Long id,
      @Valid @RequestBody AddCommentRequest request,
      Authentication authentication) {

    CommentResponse response = commentFacade.addComment(authentication.getName(), id, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}/comments")
  public ResponseEntity<List<CommentResponse>> getVideoComments(@PathVariable Long id) {
    List<CommentResponse> response = commentFacade.getVideoComments(id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{videoId}/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable Long videoId, @PathVariable Long commentId, Authentication authentication) {

    commentFacade.deleteComment(authentication.getName(), videoId, commentId);
    return ResponseEntity.noContent().build();
  }
}
