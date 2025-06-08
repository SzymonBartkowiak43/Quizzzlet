package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RateVideoRequest {
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    public RateVideoRequest() {}

    // Getters and setters
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}