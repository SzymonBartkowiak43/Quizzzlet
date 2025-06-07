package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.comment.CommentService;
import com.example.quizlecikprojekt.domain.comment.dto.CommentDto;
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

    public VideoController(CommentService commentService, VideoService videoService, UserService userService, RatingService ratingService) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userService = userService;
        this.ratingService = ratingService;
    }


    @GetMapping("/{id}")
    public String video(@PathVariable Long id, Model model, Principal principal) {
        Video video = videoService.findById(id);
        Long userId = video.getUserId();
        User user = userService.getUserById(userId);
        List<CommentDto> allCommentsByVideo = commentService.findAllDtoCommentsByVideoId(video.getId());
        String userEmail = principal.getName();
        Optional<Integer> userRating = ratingService.getUserRatingForVideo(userEmail, id);

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("url", video.getUrl());
        model.addAttribute("title", video.getTitle());
        model.addAttribute("comments", allCommentsByVideo);
        model.addAttribute("videoId", id);
        model.addAttribute("userRating", userRating.orElse(0));

        return "video";
    }

    @GetMapping("/showAll")
    public String showAllVideos(Model model) {
        List<Video> allVideos = videoService.findAll();
        List<Video> top5BestRatedVideosLast7Days = videoService.findTop4BestRatedVideosLast7Days();
        Map<Long, Double> videoRatings = allVideos.stream()
                .collect(Collectors.toMap(Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));
//       List<Video> videosFromDirectoryVideo = videoService.getVideosFromDirectoryVideo();

        model.addAttribute("videos", allVideos);
        model.addAttribute("top5BestRatedVideosLast7Days", top5BestRatedVideosLast7Days);
        model.addAttribute("videoRatings", videoRatings);
//        model.addAttribute("videosFromDirectoryVideo", videosFromDirectoryVideo);

        return "videoMenu";
    }

    @GetMapping("/search")
    public String searchVideos(@RequestParam String query, Model model) {
        List<Video> searchResults = videoService.searchVideosByTitle(query);
        Map<Long, Double> videoRatings = searchResults.stream()
                .collect(Collectors.toMap(Video::getId, video -> ratingService.getAverageRatingForVideo(video.getId())));

        model.addAttribute("videoRatings", videoRatings);
        model.addAttribute("videos", searchResults);

        return "videoMenu";
    }


    @PostMapping("/addVideo")
    public String addVideo(String url, String title, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        videoService.addVideo(url, title, user.getId());
        return "redirect:/video/showAll";
    }

    //***Commment**
    @PostMapping("/{id}/addComment")
    public String addComment(@PathVariable Long id, String content, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Video video = videoService.findById(id);
        commentService.addComment(content, user, video);
        return "redirect:/video/" + id;
    }

    @PostMapping("/{videoId}/deleteComment")
    public String deleteComment(@PathVariable Long videoId, Long commentId, Authentication authentication) {
        Comment comment = commentService.findById(commentId);
        User currentUser = userService.getUserByEmail(authentication.getName());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        if (isAdmin || comment.getUser().equals(currentUser)) {
            commentService.deleteComment(commentId);
        } else {
        }
        return "redirect:/video/" + videoId;
    }

    //***Rating***
    @PostMapping("/{videoId}/rate")
    public String rateVideo(@PathVariable Long videoId, @RequestParam int rating, Principal principal) {
        String userEmail = principal.getName();
        ratingService.addOrUpdateRating(userEmail, videoId, rating);
        return "redirect:/video/" + videoId;
    }

}