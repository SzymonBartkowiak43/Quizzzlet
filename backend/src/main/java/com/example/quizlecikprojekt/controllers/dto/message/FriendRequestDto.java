package com.example.quizlecikprojekt.controllers.dto.message;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FriendRequestDto {

    @NotNull(message = "ID adresata nie może być puste")
    @Positive(message = "ID adresata musi być dodatnie")
    private Long addresseeId;

    // Constructors
    public FriendRequestDto() {}

    public FriendRequestDto(Long addresseeId) {
        this.addresseeId = addresseeId;
    }

    // Getters and Setters
    public Long getAddresseeId() { return addresseeId; }
    public void setAddresseeId(Long addresseeId) { this.addresseeId = addresseeId; }
}