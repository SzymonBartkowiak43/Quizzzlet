package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.rating.RatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final RatingService ratingService;

    public VideoService(VideoRepository videoRepository, RatingService ratingService) {
        this.videoRepository = videoRepository;
        this.ratingService = ratingService;
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

    public List<Video> findTop4BestRatedVideosLast7Days() {
        List<Video> all = (List<Video>) videoRepository.findAll();
        return all.stream()
                .sorted((v1, v2) -> Double.compare(ratingService.getAverageRatingForVideo(v2.getId()), ratingService.getAverageRatingForVideo(v1.getId())))
                .limit(4)
                .collect(Collectors.toList());
    }
}