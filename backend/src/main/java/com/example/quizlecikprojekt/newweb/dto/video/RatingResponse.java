package com.example.quizlecikprojekt.newweb.dto.video;

public class RatingResponse {
    private Long videoId;
    private int userRating;
    private double averageRating;

    public RatingResponse() {}

    // Getters and setters
    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public int getUserRating() { return userRating; }
    public void setUserRating(int userRating) { this.userRating = userRating; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}