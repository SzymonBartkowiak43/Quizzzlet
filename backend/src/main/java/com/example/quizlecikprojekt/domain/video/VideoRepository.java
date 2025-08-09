package com.example.quizlecikprojekt.domain.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUserId(Long userId);

    long countByUserId(Long userId);

    boolean existsByUrl(String url);

    Optional<Video> findByUrl(String url);

    List<Video> findByCreatedAtAfter(LocalDateTime date);

    List<Video> findByTitleContainingIgnoreCase(String title);

    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.userId = :userId ORDER BY v.createdAt DESC")
    List<Video> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT v FROM Video v ORDER BY v.createdAt DESC")
    Page<Video> findAllOrderByCreatedAtDesc(Pageable pageable);
}
