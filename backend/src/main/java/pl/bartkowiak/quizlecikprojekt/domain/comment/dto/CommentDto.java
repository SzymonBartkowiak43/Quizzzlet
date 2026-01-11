package pl.bartkowiak.quizlecikprojekt.domain.comment.dto;

import pl.bartkowiak.quizlecikprojekt.entity.User;

import java.time.LocalDateTime;

public record CommentDto(Long id, String content, User user, LocalDateTime dateAndTime) {}
