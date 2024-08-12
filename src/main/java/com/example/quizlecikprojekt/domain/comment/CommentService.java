package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.domain.comment.Dto.CommentDto;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.comment.Dto.CommentDtoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService  {
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

    public void addComment(String content, User user, Video video) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setVideo(video);
        comment.setDateAndTime(LocalDateTime.now());
        commentRepository.save(comment);
    }
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow();
    }
}
