package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.rating.RatingService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  /**
   * Find video by ID
   * @param id Video ID
   * @return Video entity
   * @throws EntityNotFoundException if video not found
   */
  public Video findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Video ID cannot be null");
    }

    return videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video not found with id: " + id));
  }

  /**
   * Find video by ID, returns Optional
   * @param id Video ID
   * @return Optional<Video>
   */
  public Optional<Video> findOptionalById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return videoRepository.findById(id);
  }

  /**
   * Get all videos
   * @return List of all videos
   */
  public List<Video> findAll() {
    return videoRepository.findAll();
  }

  /**
   * Get paginated videos
   * @param pageable Pagination information
   * @return Page of videos
   */
  public Page<Video> findAll(Pageable pageable) {
    return videoRepository.findAll(pageable);
  }

  /**
   * Find videos by user ID
   * @param userId User ID
   * @return List of videos owned by the user
   */
  public List<Video> findByUserId(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    return videoRepository.findByUserId(userId);
  }

  /**
   * Add a new video
   * @param url Video URL
   * @param title Video title
   * @param userId Owner user ID
   * @return Created video
   * @throws IllegalArgumentException if parameters are invalid
   */
  public Video addVideo(String url, String title, Long userId) {
    validateVideoInputs(url, title, userId);

    String processedUrl = processYouTubeUrl(url.trim());
    String processedTitle = title.trim();

    // Check if video with same URL already exists
    if (videoRepository.existsByUrl(processedUrl)) {
      logger.warn("Attempt to add duplicate video URL: {}", processedUrl);
      throw new IllegalArgumentException("A video with this URL already exists");
    }

    Video video = new Video();
    video.setUrl(processedUrl);
    video.setTitle(processedTitle);
    video.setUserId(userId);
    video.setCreatedAt(LocalDateTime.now());

    Video savedVideo = videoRepository.save(video);
    logger.info("Video created successfully: {} by user: {}", savedVideo.getId(), userId);

    return savedVideo;
  }

  /**
   * Update video details
   * @param videoId Video ID
   * @param title New title
   * @param url New URL (optional)
   * @return Updated video
   * @throws EntityNotFoundException if video not found
   */
  public Video updateVideo(Long videoId, String title, String url) {
    Video video = findById(videoId);

    if (title != null && !title.trim().isEmpty()) {
      if (title.trim().length() > 200) {
        throw new IllegalArgumentException("Title cannot exceed 200 characters");
      }
      video.setTitle(title.trim());
    }

    if (url != null && !url.trim().isEmpty()) {
      String processedUrl = processYouTubeUrl(url.trim());

      // Check if another video with this URL exists
      Optional<Video> existingVideo = videoRepository.findByUrl(processedUrl);
      if (existingVideo.isPresent() && !existingVideo.get().getId().equals(videoId)) {
        throw new IllegalArgumentException("Another video with this URL already exists");
      }

      video.setUrl(processedUrl);
    }

    Video updatedVideo = videoRepository.save(video);
    logger.info("Video updated successfully: {}", videoId);

    return updatedVideo;
  }

  /**
   * Get top rated videos from last 7 days
   * @return List of top 4 best rated videos
   */
  public List<Video> findTop4BestRatedVideosLast7Days() {
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

    // Get videos from last 7 days if repository supports it
    List<Video> recentVideos = videoRepository.findByCreatedAtAfter(sevenDaysAgo);

    if (recentVideos.isEmpty()) {
      // Fallback to all videos if no recent videos
      recentVideos = videoRepository.findAll();
    }

    return recentVideos.stream()
            .sorted((v1, v2) -> Double.compare(
                    ratingService.getAverageRatingForVideoInLast7Days(v2.getId()),
                    ratingService.getAverageRatingForVideoInLast7Days(v1.getId())
            ))
            .limit(4)
            .toList();
  }

  /**
   * Search videos by title
   * @param query Search query
   * @return List of matching videos
   */
  public List<Video> searchVideosByTitle(String query) {
    if (query == null || query.trim().isEmpty()) {
      return List.of();
    }

    String cleanQuery = query.trim();

    // Use repository method if available, otherwise filter in memory
    try {
      return videoRepository.findByTitleContainingIgnoreCase(cleanQuery);
    } catch (Exception e) {
      // Fallback to in-memory filtering
      logger.warn("Repository search failed, using in-memory search", e);
      return videoRepository.findAll().stream()
              .filter(video -> video.getTitle().toLowerCase().contains(cleanQuery.toLowerCase()))
              .toList();
    }
  }

  /**
   * Search videos by title with pagination
   * @param query Search query
   * @param pageable Pagination information
   * @return Page of matching videos
   */
  public Page<Video> searchVideosByTitle(String query, Pageable pageable) {
    if (query == null || query.trim().isEmpty()) {
      return Page.empty();
    }

    return videoRepository.findByTitleContainingIgnoreCase(query.trim(), pageable);
  }

  /**
   * Delete video by ID
   * @param videoId Video ID to delete
   * @throws EntityNotFoundException if video not found
   */
  public void deleteVideo(Long videoId) {
    if (videoId == null) {
      throw new IllegalArgumentException("Video ID cannot be null");
    }

    Video video = findById(videoId); // This will throw if not found

    try {
      videoRepository.delete(video);
      logger.info("Video deleted successfully: {}", videoId);
    } catch (Exception e) {
      logger.error("Error deleting video: {}", videoId, e);
      throw new RuntimeException("Failed to delete video", e);
    }
  }

  /**
   * Check if video exists
   * @param videoId Video ID
   * @return true if video exists
   */
  public boolean existsById(Long videoId) {
    if (videoId == null) {
      return false;
    }
    return videoRepository.existsById(videoId);
  }

  /**
   * Get video count by user
   * @param userId User ID
   * @return Number of videos owned by user
   */
  public long countByUserId(Long userId) {
    if (userId == null) {
      return 0;
    }
    return videoRepository.countByUserId(userId);
  }

  /**
   * Get total video count
   * @return Total number of videos
   */
  public long count() {
    return videoRepository.count();
  }

  // Private helper methods

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

    // Validate URL format
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

    // If it's already an embed URL, return as is
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
      throw new IllegalArgumentException("Invalid video URL format. Please provide a valid YouTube URL.");
    }
  }
}