package com.example.quizlecikprojekt.domain.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDto {
    private Long id;
    private String name;
    private Long creatorId;
    private String creatorName;
    private List<Long> memberIds;
    private List<String> memberNames;

    public GroupDto(Long id, String name, Long creatorId, String creatorName, List<Long> memberIds, List<String> memberNames) {
        this.id = id;
        this.name = name;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.memberIds = memberIds;
        this.memberNames = memberNames;
    }

}