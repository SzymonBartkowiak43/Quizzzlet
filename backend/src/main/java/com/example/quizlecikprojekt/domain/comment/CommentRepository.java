package com.example.quizlecikprojekt.domain.comment;

import java.util.List;

import com.example.quizlecikprojekt.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
  List<Comment> findByVideoId(Long id);
}
