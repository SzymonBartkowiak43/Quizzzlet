package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userFacade.getAllUsers();
        return ResponseEntity.ok(users);
    }


}
