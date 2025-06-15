package com.example.quizlecikprojekt.newweb.dto.video;

import java.util.List;

public record VideoDetailsResponse (
    Long id,
    String title,
    String url,
    String ownerUsername,
    Long ownerId,
    List<CommentResponse> comments,
    int userRating,
    double averageRating,
     int totalComments
)
    {}