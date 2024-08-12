package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.rating.RatingService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<Video> findAll() {
        return (List<Video>) videoRepository.findAll();
    }
    public void addVideo(String url, String title, Long userId) {
        Video video = new Video();
        video.setUrl(url);
        video.setTitle(title);
        video.setUserId(userId);
        video.setDateAndTime(java.time.LocalDateTime.now());
        videoRepository.save(video);
    }

    public List<Video> findTop4BestRatedVideosLast7Days() {
        List<Video> all = (List<Video>) videoRepository.findAll();
        return all.stream()
                .sorted((v1, v2) -> Double.compare(ratingService.getAverageRatingForVideo(v2.getId()), ratingService.getAverageRatingForVideo(v1.getId())))
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<Video> searchVideosByTitle(String query) {
        List<Video> allVideos = (List<Video>) videoRepository.findAll();
        return allVideos.stream()
                .filter(video -> video.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Video> getVideosFromDirectoryVideo() {
        try(Stream<Path> paths = Files.walk(Paths.get("src/main/resources/static/video"))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(file -> {
                        Video video = new Video();
                        video.setTitle(file.getName());
                        video.setUrl("**/static/video/" + file.getName());
                        System.out.println(video.getUrl());
                        return video;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}