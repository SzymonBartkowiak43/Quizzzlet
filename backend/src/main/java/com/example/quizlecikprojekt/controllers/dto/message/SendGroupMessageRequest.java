package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendGroupMessageRequest {

    @NotNull(message = "ID grupy nie może być puste")
    @Positive(message = "ID grupy musi być dodatnie")
    private Long groupId;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(max = 1000, message = "Wiadomość nie może przekraczać 1000 znaków")
    private String content;

}
