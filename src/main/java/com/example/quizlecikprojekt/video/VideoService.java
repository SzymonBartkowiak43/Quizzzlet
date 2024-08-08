package com.example.quizlecikprojekt.video;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Video findById(Long id) {
        return videoRepository.findById(id).orElseThrow(() -> new RuntimeException("Video not found"));
    }
    public Video findMainVideo() {
        return videoRepository.findById(1L).orElseThrow(() -> new RuntimeException("Video not found"));
    }
    public List<Video> findAll() {
        return (List<Video>) videoRepository.findAll();
    }
    public Video addVideo(String url, String title, Long userId) {
        Video video = new Video();
        video.setUrl(url);
        video.setTitle(title);
        video.setUserId(userId);
        return videoRepository.save(video);
    }
}