package com.example.quizlecikprojekt.domain.video;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

  boolean existsByUrl(String url);

  List<Video> findByCreatedAtAfter(LocalDateTime date);

  List<Video> findByTitleContainingIgnoreCase(String title);
}
