package com.example.quizlecikprojekt.domain.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMessageDto {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;
    private String content;
    private String createdAt;

    public GroupMessageDto(Long id, Long groupId, Long senderId, String senderName, String content, String createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Gettery, settery
}