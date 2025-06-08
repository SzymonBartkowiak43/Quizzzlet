package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.newweb.dto.*;
import com.example.quizlecikprojekt.newweb.dto.profil.*;
import com.example.quizlecikprojekt.newweb.dto.profil.UserProfileResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileRestController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileRestController.class);

    private final UserService userService;

    public ProfileRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String email = authentication.getName();
            Optional<UserDto> userDtoOptional = userService.findCredentialsByEmail(email);

            if (userDtoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }

            UserDto userDto = userDtoOptional.get();
            UserProfileResponse response = mapToUserProfileResponse(userDto);

            logger.info("Profile retrieved for user: {}", email);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));

        } catch (Exception e) {
            logger.error("Error retrieving profile for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve profile"));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String email = authentication.getName();

            // Weryfikuj obecne hasło
            if (!userService.verifyCurrentPassword(email, request.getCurrentPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Current password is incorrect"));
            }

            User currentUser = userService.getUserByEmail(email);
            UserDto userDto = new UserDto();
            userDto.setEmail(email);

            boolean updated = false;

            // Sprawdź czy nazwa użytkownika się zmieniła
            if (request.getUserName() != null &&
                    !request.getUserName().trim().isEmpty() &&
                    !request.getUserName().equals(currentUser.getUserName())) {

                // Sprawdź czy nowa nazwa użytkownika już istnieje
                if (userService.usernameExists(request.getUserName())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error("Username already exists"));
                }

                userDto.setUserName(request.getUserName());
                updated = true;
                logger.debug("Username will be updated for user: {} to: {}", email, request.getUserName());
            }

            // Sprawdź czy hasło się zmieniło
            if (request.getNewPassword() != null &&
                    !request.getNewPassword().trim().isEmpty() &&
                    !request.getNewPassword().equals(request.getCurrentPassword())) {

                userDto.setPassword(request.getNewPassword());
                updated = true;
                logger.debug("Password will be updated for user: {}", email);
            }

            if (!updated) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("No changes detected"));
            }

            // Wykonaj aktualizację
            userService.updateUser(userDto);

            // Pobierz zaktualizowane dane
            Optional<UserDto> updatedUserDtoOptional = userService.findCredentialsByEmail(email);
            if (updatedUserDtoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Failed to retrieve updated profile"));
            }

            UserProfileResponse response = mapToUserProfileResponse(updatedUserDtoOptional.get());

            logger.info("Profile updated successfully for user: {}", email);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));

        } catch (Exception e) {
            logger.error("Error updating profile for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update profile"));
        }
    }

    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<PasswordVerificationResponse>> verifyPassword(
            @Valid @RequestBody PasswordVerificationRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String email = authentication.getName();
            boolean isValid = userService.verifyCurrentPassword(email, request.getPassword());

            PasswordVerificationResponse response = new PasswordVerificationResponse();
            response.setValid(isValid);

            if (isValid) {
                logger.debug("Password verification successful for user: {}", email);
                return ResponseEntity.ok(ApiResponse.success("Password verified", response));
            } else {
                logger.debug("Password verification failed for user: {}", email);
                return ResponseEntity.ok(ApiResponse.success("Password verification completed", response));
            }

        } catch (Exception e) {
            logger.error("Error verifying password for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to verify password"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String email = authentication.getName();

            // Weryfikuj obecne hasło
            if (!userService.verifyCurrentPassword(email, request.getCurrentPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Current password is incorrect"));
            }

            // Sprawdź czy nowe hasło jest różne od obecnego
            if (request.getCurrentPassword().equals(request.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("New password must be different from current password"));
            }

            UserDto userDto = new UserDto();
            userDto.setEmail(email);
            userDto.setPassword(request.getNewPassword());

            userService.updateUser(userDto);

            logger.info("Password changed successfully for user: {}", email);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "Password updated"));

        } catch (Exception e) {
            logger.error("Error changing password for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change password"));
        }
    }

    @PostMapping("/change-username")
    public ResponseEntity<ApiResponse<UserProfileResponse>> changeUsername(
            @Valid @RequestBody ChangeUsernameRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String email = authentication.getName();

            // Weryfikuj obecne hasło
            if (!userService.verifyCurrentPassword(email, request.getCurrentPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Current password is incorrect"));
            }

            User currentUser = userService.getUserByEmail(email);

            // Sprawdź czy nowa nazwa użytkownika jest różna
            if (request.getNewUsername().equals(currentUser.getUserName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("New username must be different from current username"));
            }

            // Sprawdź czy nowa nazwa użytkownika już istnieje
            if (userService.usernameExists(request.getNewUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Username already exists"));
            }

            UserDto userDto = new UserDto();
            userDto.setEmail(email);
            userDto.setUserName(request.getNewUsername());

            userService.updateUser(userDto);

            // Pobierz zaktualizowane dane
            Optional<UserDto> updatedUserDtoOptional = userService.findCredentialsByEmail(email);
            if (updatedUserDtoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Failed to retrieve updated profile"));
            }

            UserProfileResponse response = mapToUserProfileResponse(updatedUserDtoOptional.get());

            logger.info("Username changed successfully for user: {} to: {}", email, request.getNewUsername());
            return ResponseEntity.ok(ApiResponse.success("Username changed successfully", response));

        } catch (Exception e) {
            logger.error("Error changing username for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change username"));
        }
    }

    // Helper method
    private UserProfileResponse mapToUserProfileResponse(UserDto userDto) {
        UserProfileResponse response = new UserProfileResponse();
        response.setEmail(userDto.getEmail());
        response.setUserName(userDto.getUserName());
        // Nie zwracamy hasła w response ze względów bezpieczeństwa
        return response;
    }
}
