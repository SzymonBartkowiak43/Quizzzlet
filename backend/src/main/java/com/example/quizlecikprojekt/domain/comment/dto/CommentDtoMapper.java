package com.example.quizlecikprojekt.domain.comment.dto;

import com.example.quizlecikprojekt.domain.comment.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentDtoMapper {

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getContent(),comment.getUser(),comment.getCreatedAt());
    }

    public List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream().map(this::toDto).toList();
    }
}
