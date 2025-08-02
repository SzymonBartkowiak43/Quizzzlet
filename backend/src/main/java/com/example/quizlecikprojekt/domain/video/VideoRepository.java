package com.example.quizlecikprojekt.domain.video;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {}
