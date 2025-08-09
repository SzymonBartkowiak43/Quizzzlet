package com.example.quizlecikprojekt.newweb.dto.video;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public record VideosListResponse(
        List<VideoSummaryResponse> videos,
        List<VideoSummaryResponse> topRatedVideos,
        Integer totalVideos
) {}

