package com.example.quizlecikprojekt.newweb.loginandregister;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRole;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.user.UserRoleRepository;
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
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserService userFacade;
    private final PasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto dto) {

        User newUser = userFacade.createNewUser(
                new UserRegisterDto(dto.email(), dto.name(), dto.password())
        );

        Set<String> roleNames = newUser.getRoles().stream()
                .map(UserRole::getName).collect(java.util.stream.Collectors.toSet());

        UserResponseDto body = new UserResponseDto(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getName(),
                roleNames
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
