package com.example.quizlecikprojekt.newweb.dto.video;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VideosListResponse {
    private List<VideoSummaryResponse> videos;
    private List<VideoSummaryResponse> topRatedVideos;
    private int totalVideos;
}