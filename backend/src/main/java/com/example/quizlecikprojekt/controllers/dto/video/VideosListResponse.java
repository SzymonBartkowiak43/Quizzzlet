package com.example.quizlecikprojekt.controllers.dto.video;

import java.util.List;

public record VideosListResponse(
    List<VideoSummaryResponse> videos,
    List<VideoSummaryResponse> topRatedVideos,
    Integer totalVideos) {}
