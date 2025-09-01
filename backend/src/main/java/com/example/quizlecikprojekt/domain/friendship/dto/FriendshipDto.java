package com.example.quizlecikprojekt.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendshipDto {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private String requesterEmail;
    private Long addresseeId;
    private String addresseeName;
    private String addresseeEmail;
    private String status;
}