package com.example.quizlecikprojekt.domain.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGroupRequest {
    private String name;
    private List<Long> memberIds;

    public CreateGroupRequest() {}

    public CreateGroupRequest(String name, List<Long> memberIds) {
        this.name = name;
        this.memberIds = memberIds;
    }

    // Gettery, settery
}
