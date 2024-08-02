package com.example.quizlecikprojekt.comment.Dto;

import com.example.quizlecikprojekt.comment.Comment;
import com.example.quizlecikprojekt.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentDtoMapper {
    private final UserService userService;

    public CommentDtoMapper(UserService userService) {
        this.userService = userService;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setUser(userService.getUserByid(comment.getUserId()));
        commentDto.setDateAndTime(comment.getDateAndTime());
        return commentDto;
    }

    public List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream().map(this::toDto).toList();
    }
}
