package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.domain.comment.Dto.CommentDto;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.comment.Dto.CommentDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService  {
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper) {
        this.commentRepository = commentRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public List<CommentDto> findAllDtoCommentsByVideoId(Long id) {
        LOGGER.info("Entering findAllDtoCommentsByVideoId with id: {}", id);
        List<Comment> ListOfComments = commentRepository.findByVideoId(id);
        List<CommentDto> commentDtos = commentDtoMapper.toDto(ListOfComments);
        LOGGER.info("Exiting findAllDtoCommentsByVideoId with result: {}", commentDtos);
        return commentDtos;
    }

    public void addComment(String content, User user, Video video) {
        LOGGER.info("Entering addComment with content: {}, user: {}, video: {}", content, user, video);
        if(content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        } else {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUser(user);
            comment.setVideo(video);
            comment.setDateAndTime(LocalDateTime.now());
            commentRepository.save(comment);
            LOGGER.info("Comment added successfully");
        }

    }
    public void deleteComment(Long id) {
        LOGGER.info("Entering deleteComment with id: {}", id);
        commentRepository.deleteById(id);
        LOGGER.info("Comment deleted successfully");
    }

    public Comment findById(Long id) {
        LOGGER.info("Entering findById with id: {}", id);
        Comment comment = commentRepository.findById(id).orElseThrow();
        LOGGER.info("Exiting findById with result: {}", comment);
        return comment;
    }
}
