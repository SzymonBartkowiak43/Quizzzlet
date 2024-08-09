package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.domain.video.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByVideo(Video video);
    List<Comment> findByVideoId(Long id);
}
