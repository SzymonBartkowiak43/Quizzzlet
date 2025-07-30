package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.PasswordValidator;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.login.LoginRequest;
import com.example.quizlecikprojekt.newweb.dto.login.LoginResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            List<String> constraintViolations = PasswordValidator.getConstraintViolations(userRegistrationDto.password());
            if (!constraintViolations.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Password validation failed", constraintViolations));
            }

            if (userService.emailExists(userRegistrationDto.email())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email already exists"));
            }

            if (userService.usernameExists(userRegistrationDto.username())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Username already exists"));
            }

            userService.registerUserWithDefaultRole(userRegistrationDto);

            logger.info("User registered successfully: {}", userRegistrationDto.username());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", "User created"));

        } catch (Exception e) {
            logger.error("Registration failed for user: {}", userRegistrationDto.username(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponse loginResponse = new LoginResponse(
                    authentication.getName(),
                    "Login successful"
            );

            logger.info("User logged in successfully: {}", loginRequest.username());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));

        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.username(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }
}