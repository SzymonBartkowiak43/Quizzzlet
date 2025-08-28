package com.example.quizlecikprojekt.domain.friendship.entity;

public record PrivateMessageDto(Long id, Long senderId, Long recipientId, String content, String timestamp){}