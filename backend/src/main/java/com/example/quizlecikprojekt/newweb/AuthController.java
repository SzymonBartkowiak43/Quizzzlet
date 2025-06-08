package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.PasswordValidator;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.LoginRequest;
import com.example.quizlecikprojekt.newweb.dto.LoginResponse;
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
            // Validate password
            List<String> constraintViolations = PasswordValidator.getConstraintViolations(userRegistrationDto.getPassword());
            if (!constraintViolations.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Password validation failed", constraintViolations));
            }

            // Check if email exists
            if (userService.emailExists(userRegistrationDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email already exists"));
            }

            // Check if username exists
            if (userService.usernameExists(userRegistrationDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Username already exists"));
            }

            // Register user
            userService.registerUserWithDefaultRole(userRegistrationDto);

            logger.info("User registered successfully: {}", userRegistrationDto.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", "User created"));

        } catch (Exception e) {
            logger.error("Registration failed for user: {}", userRegistrationDto.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Here you could generate JWT token if needed
            LoginResponse loginResponse = new LoginResponse(
                    authentication.getName(),
                    "Login successful"
            );

            logger.info("User logged in successfully: {}", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));

        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "Session cleared"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser");

        if (isLoggedIn) {
            return ResponseEntity.ok(ApiResponse.success("User authenticated", authentication.getName()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }
    }
}