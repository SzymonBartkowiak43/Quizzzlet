package com.example.quizlecikprojekt.newweb.dto.video;

import java.util.List;

public class VideosListResponse {
    private List<VideoSummaryResponse> videos;
    private List<VideoSummaryResponse> topRatedVideos;
    private int totalVideos;

    public VideosListResponse() {}

    // Getters and setters
    public List<VideoSummaryResponse> getVideos() { return videos; }
    public void setVideos(List<VideoSummaryResponse> videos) { this.videos = videos; }

    public List<VideoSummaryResponse> getTopRatedVideos() { return topRatedVideos; }
    public void setTopRatedVideos(List<VideoSummaryResponse> topRatedVideos) { this.topRatedVideos = topRatedVideos; }

    public int getTotalVideos() { return totalVideos; }
    public void setTotalVideos(int totalVideos) { this.totalVideos = totalVideos; }
}