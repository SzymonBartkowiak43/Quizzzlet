package com.example.quizlecikprojekt.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendDto {
    private Long id;
    private String email;
    private String name;
}