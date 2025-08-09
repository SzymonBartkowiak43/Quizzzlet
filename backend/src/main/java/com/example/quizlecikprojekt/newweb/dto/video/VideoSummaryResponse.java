package com.example.quizlecikprojekt.newweb.dto.video;

public record VideoSummaryResponse(
        Long id,
        String title,
        String url,
        String ownerName,
        Long ownerId,
        Double averageRating
) {}
