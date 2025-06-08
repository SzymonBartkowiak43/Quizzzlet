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
        return videoRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Video not found");
        });
    }

    public List<Video> findAll() {
        return (List<Video>) videoRepository.findAll();
    }

    public Video addVideo(String url, String title, Long userId) {

        try {
            url = "https://www.youtube.com/embed/" + url.substring(url.indexOf("v=") + 2);
        } catch (Exception e) {
            throw new RuntimeException("Invalid url");
        }

        Video video = new Video();
        video.setUrl(url);
        video.setTitle(title);
        video.setUserId(userId);
        video.setDateAndTime(java.time.LocalDateTime.now());
        videoRepository.save(video);
        return video;
    }

    public List<Video> findTop4BestRatedVideosLast7Days() {
        List<Video> all = (List<Video>) videoRepository.findAll();
        return all.stream()
                .sorted((v1, v2) -> Double.compare(ratingService.getAverageRatingForVideoInLast7Days(v2.getId()), ratingService.getAverageRatingForVideoInLast7Days(v1.getId())))
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<Video> searchVideosByTitle(String query) {
        List<Video> allVideos = (List<Video>) videoRepository.findAll();
        return allVideos.stream()
                .filter(video -> video.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

}