package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDtoMapper;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;

    public CommentService(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper) {
        this.commentRepository = commentRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public List<CommentDto> findAllDtoCommentsByVideoId(Long id) {
        List<Comment> ListOfComments = commentRepository.findByVideoId(id);
        return commentDtoMapper.toDto(ListOfComments);
    }

    public Comment addComment(String content, User user, Video video) {
        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        } else {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUser(user);
            comment.setVideo(video);
            comment.setCreatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow();
    }
}
