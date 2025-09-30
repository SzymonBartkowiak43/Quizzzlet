package com.example.quizlecikprojekt.controllers.dto.message;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {

    @NotNull(message = "ID adresata nie może być puste")
    @Positive(message = "ID adresata musi być dodatnie")
    private Long addresseeId;

}