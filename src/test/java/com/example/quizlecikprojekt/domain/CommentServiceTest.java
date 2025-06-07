package com.example.quizlecikprojekt.domain;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.comment.CommentRepository;
import com.example.quizlecikprojekt.domain.comment.CommentService;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDtoMapper;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentDtoMapper commentDtoMapper;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidVideoIdTest() {
        // Given
        Long videoId = 1L;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        List<CommentDto> expectedDtos = Arrays.asList(new CommentDto(), new CommentDto());

        when(commentRepository.findByVideoId(videoId)).thenReturn(comments);
        when(commentDtoMapper.toDto(comments)).thenReturn(expectedDtos);

        // When
        List<CommentDto> result = commentService.findAllDtoCommentsByVideoId(videoId);

        // Then
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findByVideoId(videoId);
        verify(commentDtoMapper, times(1)).toDto(comments);
    }

    @Test
    void givenInvalidVideoIdEmptyListTest() {
        // Given
        Long videoId = 1L;

        when(commentRepository.findByVideoId(videoId)).thenReturn(Collections.emptyList());
        when(commentDtoMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<CommentDto> result = commentService.findAllDtoCommentsByVideoId(videoId);

        // Then
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findByVideoId(videoId);
        verify(commentDtoMapper, times(1)).toDto(Collections.emptyList());
    }

    @Test
    void givenValidContentUserAndVideoAddCommentTest() {
        // Given
        String content = "Test Comment";
        User user = new User();
        Video video = new Video();

        // When
        commentService.addComment(content, user, video);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void givenNullContent_whenAddComment_thenThrowException() {
        // Given
        User user = new User();
        Video video = new Video();

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment("", user, video);
        });
    }


}
