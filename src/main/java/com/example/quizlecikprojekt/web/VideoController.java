package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.comment.Comment;
import com.example.quizlecikprojekt.comment.CommentService;
import com.example.quizlecikprojekt.comment.Dto.CommentDto;
import com.example.quizlecikprojekt.user.User;
import com.example.quizlecikprojekt.user.UserService;
import com.example.quizlecikprojekt.video.Video;
import com.example.quizlecikprojekt.video.VideoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class VideoController {
    private final CommentService commentService;
    private final VideoService videoService;
    private final UserService userService;

    public VideoController(CommentService commentService, VideoService videoService, UserService userService) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userService = userService;
    }

    @GetMapping("/video")
    public String videos(Model model) {
        Video video = videoService.findMainVideo();
        Long videoId = video.getId();
        Long userId = video.getUserId();
        User user = userService.getUserByid(userId);
        List<CommentDto> allCommentsByVideo = commentService.findAllDtoCommentsByVideoId(video.getId());

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("url", video.getUrl());
        model.addAttribute("title", video.getTitle());
        model.addAttribute("comments", allCommentsByVideo);
        model.addAttribute("videoId", videoId);


        return "video";
    }

    @GetMapping("/video/{id}")
    public String video(@PathVariable Long id, Model model) {
        Video video = videoService.findById(id);
        Long userId = video.getUserId();
        User user = userService.getUserByid(userId);
        List<CommentDto> allCommentsByVideo = commentService.findAllDtoCommentsByVideoId(video.getId());

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("url", video.getUrl());
        model.addAttribute("title", video.getTitle());
        model.addAttribute("comments", allCommentsByVideo);
        model.addAttribute("videoId", id);

        return "video";
    }

    @PostMapping("/video/{id}/addComment")
    public String addComment(@PathVariable Long id, String content, User user) {
        Video video = videoService.findById(id);
        commentService.addComment(content, user, video);
        return "redirect:/video/" + id;
    }

    @PostMapping("/video/{videoId}/deleteComment")
    public String deleteComment(@PathVariable Long videoId, Long commentId, Principal principal) {
        Comment comment = commentService.findById(commentId);
        User currentUser = userService.getUserByEmail(principal.getName());
        if (comment.getUserId().equals(currentUser.getId())) {
            commentService.deleteComment(commentId);
        }
        return "redirect:/video/" + videoId;
    }

    @GetMapping("/video/showAll")
    public String showAllVideos(Model model) {
        List<Video> allVideos = videoService.findAll();
        model.addAttribute("videos", allVideos);
        return "videoMenu";
    }



}