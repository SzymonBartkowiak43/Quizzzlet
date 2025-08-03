package com.example.quizlecikprojekt.newweb.loginandregister;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.domain.user.dto.UserResponseDto;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userFacade;
    private final PasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto registerUserDto) {
        String encodedPassword = bCryptPasswordEncoder.encode(registerUserDto.password());

        User newUser = userFacade.createNewUser(
                new UserRegisterDto(registerUserDto.email(), registerUserDto.name(), encodedPassword));

        UserResponseDto registerResult = new UserResponseDto(newUser.getId(), newUser.getEmail(), newUser.getEmail(), new HashSet<>());

        return ResponseEntity.status(HttpStatus.CREATED).body(registerResult);
    }
}
