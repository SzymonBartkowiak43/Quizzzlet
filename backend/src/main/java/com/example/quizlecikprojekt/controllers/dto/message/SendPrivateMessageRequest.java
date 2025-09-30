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
public class SendPrivateMessageRequest {

    @NotNull(message = "ID odbiorcy nie może być puste")
    @Positive(message = "ID odbiorcy musi być dodatnie")
    private Long recipientId;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(max = 1000, message = "Wiadomość nie może przekraczać 1000 znaków")
    private String content;

}
