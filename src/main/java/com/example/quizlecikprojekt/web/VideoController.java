package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.comment.CommentService;
import com.example.quizlecikprojekt.domain.comment.Dto.CommentDto;
import com.example.quizlecikprojekt.domain.rating.RatingService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/video")
@Controller
public class VideoController {
    private final CommentService commentService;
    private final VideoService videoService;
    private final UserService userService;
    private final RatingService ratingService;
    private final static Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

    public VideoController(CommentService commentService, VideoService videoService, UserService userService, RatingService ratingService) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userService = userService;
        this.ratingService = ratingService;
    }


    @GetMapping("/{id}")
    public String video(@PathVariable Long id, Model model, Principal principal) {
        LOGGER.info("Entering video method with id: {}", id);
        Video video = videoService.findById(id);
        Long userId = video.getUserId();
        User user = userService.getUserByid(userId);
        List<CommentDto> allCommentsByVideo = commentService.findAllDtoCommentsByVideoId(video.getId());
        String userEmail = principal.getName();
        Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, id);

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("url", video.getUrl());
        model.addAttribute("title", video.getTitle());
        model.addAttribute("comments", allCommentsByVideo);
        model.addAttribute("videoId", id);
        model.addAttribute("userRating", userRating.orElse(0));

        LOGGER.info("Returning video view for id: {}", id);
        return "video";
    }

    @GetMapping("/showAll")
    public String showAllVideos(Model model) {
        LOGGER.info("Entering showAllVideos method");
        List<Video> allVideos = videoService.findAll();
        List<Video> top5BestRatedVideosLast7Days = videoService.findTop4BestRatedVideosLast7Days();
        Map<Long, Double> videoRatings = allVideos.stream()
                .collect(Collectors.toMap(Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));
//       List<Video> videosFromDirectoryVideo = videoService.getVideosFromDirectoryVideo();

        model.addAttribute("videos", allVideos);
        model.addAttribute("top5BestRatedVideosLast7Days", top5BestRatedVideosLast7Days);
        model.addAttribute("videoRatings", videoRatings);
//        model.addAttribute("videosFromDirectoryVideo", videosFromDirectoryVideo);

        LOGGER.info("Returning videoMenu view with all videos");
        return "videoMenu";
    }

    @GetMapping("/search")
    public String searchVideos(@RequestParam String query, Model model) {
        LOGGER.info("Entering searchVideos method with query: {}", query);
        List<Video> searchResults = videoService.searchVideosByTitle(query);
        Map<Long, Double> videoRatings = searchResults.stream()
                .collect(Collectors.toMap(Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));

        model.addAttribute("videoRatings", videoRatings);
        model.addAttribute("videos", searchResults);

        LOGGER.info("Returning videoMenu view with search results: {}", searchResults);
        return "videoMenu";
    }


    @PostMapping("/addVideo")
    public String addVideo(String url, String title, Principal principal) {
        LOGGER.info("Entering addVideo method with url: {}, title: {}", url, title);
        User user = userService.getUserByEmail(principal.getName());
        videoService.addVideo(url, title, user.getId());
        LOGGER.info("Video added with url: {}, title: {}", url, title);
        return "redirect:/video/showAll";
    }

    //***Commment**
    @PostMapping("/{id}/addComment")
    public String addComment(@PathVariable Long id, String content, Principal principal) {
        LOGGER.info("Entering addComment method for video id: {} with content: {}", id, content);
        User user = userService.getUserByEmail(principal.getName());
        Video video = videoService.findById(id);
        commentService.addComment(content, user, video);
        LOGGER.info("Comment added for video id: {}", id);
        return "redirect:/video/" + id;
    }

    @PostMapping("/{videoId}/deleteComment")
    public String deleteComment(@PathVariable Long videoId, Long commentId, Authentication authentication) {
        LOGGER.info("Entering deleteComment method for video id: {} and comment id: {}", videoId, commentId);
        Comment comment = commentService.findById(commentId);
        User currentUser = userService.getUserByEmail(authentication.getName());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        if (isAdmin || comment.getUser().equals(currentUser)) {
            commentService.deleteComment(commentId);
            LOGGER.info("Comment deleted for video id: {} and comment id: {}", videoId, commentId);
        } else {
            LOGGER.warn("Unauthorized attempt to delete comment id: {} for video id: {}", commentId, videoId);
        }
        return "redirect:/video/" + videoId;
    }

    //***Rating***
    @PostMapping("/{videoId}/rate")
    public String rateVideo(@PathVariable Long videoId, @RequestParam int rating, Principal principal) {
        LOGGER.info("Entering rateVideo method for video id: {} with rating: {}", videoId, rating);
        String userEmail = principal.getName();
        ratingService.addOrUpdateRating(userEmail, videoId, rating);
        LOGGER.info("Rating added/updated for video id: {} with rating: {}", videoId, rating);
        return "redirect:/video/" + videoId;
    }

}