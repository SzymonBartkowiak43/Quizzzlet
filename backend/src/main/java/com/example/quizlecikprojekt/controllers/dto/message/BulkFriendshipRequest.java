package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class BulkFriendshipRequest {

    @NotBlank(message = "Operacja nie może być pusta")
    @Pattern(regexp = "^(send_requests|remove_friends)$",
            message = "Dozwolone operacje: send_requests, remove_friends")
    private String operation;

    @NotEmpty(message = "Lista użytkowników nie może być pusta")
    private List<Long> targetUserIds;

    // Constructors
    public BulkFriendshipRequest() {}

    public BulkFriendshipRequest(String operation, List<Long> targetUserIds) {
        this.operation = operation;
        this.targetUserIds = targetUserIds;
    }

    // Getters and Setters
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public List<Long> getTargetUserIds() { return targetUserIds; }
    public void setTargetUserIds(List<Long> targetUserIds) { this.targetUserIds = targetUserIds; }
}
