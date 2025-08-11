package com.example.quizlecikprojekt.controllers.loginandregister;

import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.domain.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class RegisterController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserRegisterDto userRegisterDto) {

    UserResponseDto body = userService.createNewUser(userRegisterDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}
