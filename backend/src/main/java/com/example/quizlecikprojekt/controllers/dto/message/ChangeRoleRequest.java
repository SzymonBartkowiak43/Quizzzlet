package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;

public class ChangeRoleRequest {

    @NotNull(message = "Rola nie może być pusta")
    private GroupRole role;

    public ChangeRoleRequest() {}

    public ChangeRoleRequest(GroupRole role) {
        this.role = role;
    }

    public GroupRole getRole() { return role; }
    public void setRole(GroupRole role) { this.role = role; }
}