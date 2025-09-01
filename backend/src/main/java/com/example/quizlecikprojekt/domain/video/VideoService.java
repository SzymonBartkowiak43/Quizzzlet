package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.controllers.dto.video.VideoSummaryResponse;
import com.example.quizlecikprojekt.domain.rating.RatingFacade;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Video;
import jakarta.persistence.EntityNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class VideoService {

  private static final Pattern YOUTUBE_WATCH_PATTERN =
      Pattern.compile("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})");
  private static final Pattern YOUTUBE_EMBED_PATTERN =
      Pattern.compile("youtube\\.com/embed/([a-zA-Z0-9_-]{11})");

  private final VideoRepository videoRepository;
  private final RatingFacade ratingFacade;


  public Video findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Video ID cannot be null");
    }

    return videoRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Video not found with id: " + id));
  }

  public List<Video> findAll() {
    return videoRepository.findAll();
  }

  public Video addVideo(String url, String title, Long userId) {
    validateVideoInputs(url, title, userId);

    String processedUrl = processYouTubeUrl(url.trim());
    String processedTitle = title.trim();

    if (videoRepository.existsByUrl(processedUrl)) {
      throw new IllegalArgumentException("A video with this URL already exists");
    }

    Video video = new Video();
    video.setUrl(processedUrl);
    video.setTitle(processedTitle);
    video.setUserId(userId);

    return videoRepository.save(video);
  }

  public List<Video> findTop4BestRatedVideosLast7Days() {
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

    List<Video> recentVideos = videoRepository.findByCreatedAtAfter(sevenDaysAgo);

    if (recentVideos.isEmpty()) {
      recentVideos = videoRepository.findAll();
    }

    return recentVideos.stream()
        .sorted(
            (v1, v2) ->
                Double.compare(
                        ratingFacade.getAverageRatingForVideoInLast7Days(v2.getId()),
                        ratingFacade.getAverageRatingForVideoInLast7Days(v1.getId())))
        .limit(4)
        .toList();
  }

  public List<Video> searchVideosByTitle(String query) {
    if (query == null || query.trim().isEmpty()) {
      return List.of();
    }

    String cleanQuery = query.trim();

    try {
      return videoRepository.findByTitleContainingIgnoreCase(cleanQuery);
    } catch (Exception e) {
      return videoRepository.findAll().stream()
          .filter(video -> video.getTitle().toLowerCase().contains(cleanQuery.toLowerCase()))
          .toList();
    }
  }

  public void deleteVideo(Long videoId) {
    if (videoId == null) {
      throw new IllegalArgumentException("Video ID cannot be null");
    }

    Video video = findById(videoId);

    try {
      videoRepository.delete(video);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete video", e);
    }
  }

  private void validateVideoInputs(String url, String title, Long userId) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("Video URL is required");
    }

    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Video title is required");
    }

    if (title.trim().length() > 200) {
      throw new IllegalArgumentException("Title cannot exceed 200 characters");
    }

    if (userId == null) {
      throw new IllegalArgumentException("User ID is required");
    }

    try {
      new URL(url.trim());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid URL format");
    }
  }

  private String processYouTubeUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("URL cannot be empty");
    }

    String cleanUrl = url.trim();

    Matcher embedMatcher = YOUTUBE_EMBED_PATTERN.matcher(cleanUrl);
    if (embedMatcher.find()) {
      return cleanUrl;
    }

    Matcher watchMatcher = YOUTUBE_WATCH_PATTERN.matcher(cleanUrl);
    if (watchMatcher.find()) {
      String videoId = watchMatcher.group(1);
      return "https://www.youtube.com/embed/" + videoId;
    }

    try {
      new URL(cleanUrl);
      return cleanUrl;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Invalid video URL format. Please provide a valid YouTube URL.");
    }
  }

  public Map<Long, Double> buildVideoRatingsMap(List<Video> videos) {
    return videos.stream()
            .collect(
                    Collectors.toMap(
                            Video::getId, video -> ratingFacade.getAverageRatingForVideo(video.getId())));
  }

  public VideoSummaryResponse mapToVideoSummary(Video video, Map<Long, Double> videoRatings, User owner) {
    try {
      return new VideoSummaryResponse(
              video.getId(),
              video.getTitle(),
              video.getUrl(),
              owner.getName(),
              video.getUserId(),
              videoRatings.getOrDefault(video.getId(), 0.0));
    } catch (Exception e) {
      return new VideoSummaryResponse(
              video.getId(),
              video.getTitle(),
              video.getUrl(),
              "Unknown",
              video.getUserId(),
              videoRatings.getOrDefault(video.getId(), 0.0));
    }
  }

}
