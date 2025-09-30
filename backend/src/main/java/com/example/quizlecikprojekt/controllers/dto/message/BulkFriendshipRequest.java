package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkFriendshipRequest {

    @NotBlank(message = "Operacja nie może być pusta")
    @Pattern(regexp = "^(send_requests|remove_friends)$",
            message = "Dozwolone operacje: send_requests, remove_friends")
    private String operation;

    @NotEmpty(message = "Lista użytkowników nie może być pusta")
    private List<Long> targetUserIds;

}
