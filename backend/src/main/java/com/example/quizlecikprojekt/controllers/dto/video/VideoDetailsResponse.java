package com.example.quizlecikprojekt.controllers.dto.video;

import java.util.List;

public record VideoDetailsResponse(
    Long id,
    String title,
    String url,
    String ownerName,
    Long ownerId,
    List<CommentResponse> comments,
    Integer userRating,
    Double averageRating,
    Integer commentsCount) {}
