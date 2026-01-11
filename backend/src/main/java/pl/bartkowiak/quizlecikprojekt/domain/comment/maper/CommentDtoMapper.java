package pl.bartkowiak.quizlecikprojekt.domain.comment.maper;

import pl.bartkowiak.quizlecikprojekt.entity.Comment;
import pl.bartkowiak.quizlecikprojekt.domain.comment.dto.CommentDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CommentDtoMapper {

  public CommentDto toDto(Comment comment) {
    return new CommentDto(
        comment.getId(), comment.getContent(), comment.getUser(), comment.getCreatedAt());
  }

  public List<CommentDto> toDto(List<Comment> comments) {
    return comments.stream().map(this::toDto).toList();
  }
}
