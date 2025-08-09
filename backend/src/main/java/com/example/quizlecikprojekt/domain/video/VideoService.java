package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.rating.RatingService;
import jakarta.persistence.EntityNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoService {

  private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

  private static final Pattern YOUTUBE_WATCH_PATTERN =
      Pattern.compile("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})");
  private static final Pattern YOUTUBE_EMBED_PATTERN =
      Pattern.compile("youtube\\.com/embed/([a-zA-Z0-9_-]{11})");

  private final VideoRepository videoRepository;
  private final RatingService ratingService;

  public VideoService(VideoRepository videoRepository, RatingService ratingService) {
    this.videoRepository = videoRepository;
    this.ratingService = ratingService;
  }

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
      logger.warn("Attempt to add duplicate video URL: {}", processedUrl);
      throw new IllegalArgumentException("A video with this URL already exists");
    }

    Video video = new Video();
    video.setUrl(processedUrl);
    video.setTitle(processedTitle);
    video.setUserId(userId);

    Video savedVideo = videoRepository.save(video);
    logger.info("Video created successfully: {} by user: {}", savedVideo.getId(), userId);

    return savedVideo;
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
                    ratingService.getAverageRatingForVideoInLast7Days(v2.getId()),
                    ratingService.getAverageRatingForVideoInLast7Days(v1.getId())))
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
      logger.warn("Repository search failed, using in-memory search", e);
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
      logger.info("Video deleted successfully: {}", videoId);
    } catch (Exception e) {
      logger.error("Error deleting video: {}", videoId, e);
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
}
