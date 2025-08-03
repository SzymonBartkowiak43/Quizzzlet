package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.profil.request.ChangePasswordRequest;
import com.example.quizlecikprojekt.newweb.dto.profil.request.ChangeUsernameRequest;
import com.example.quizlecikprojekt.newweb.dto.profil.request.PasswordVerificationRequest;
import com.example.quizlecikprojekt.newweb.dto.profil.request.UpdateProfileRequest;
import com.example.quizlecikprojekt.newweb.dto.profil.response.PasswordVerificationResponse;
import com.example.quizlecikprojekt.newweb.dto.profil.response.UserProfileResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();
      UserDto userDto = userService.findByEmail(email);

      UserProfileResponse response = mapToUserProfileResponse(userDto);

      logger.info("Profile retrieved for user: {}", email);
      return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));

    } catch (Exception e) {
      logger.error(
          "Error retrieving profile for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to retrieve profile"));
    }
  }

  @PutMapping
  public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
      @Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();

      if (!userService.verifyCurrentPassword(email, request.currentPassword())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Current password is incorrect"));
      }

      UserDto userDto = userService.findByEmail(email);

      boolean updated = false;

      if (request.userName() != null
          && !request.userName().trim().isEmpty()
          && !request.userName().equals(userDto.name())) {

        if (userService.usernameExists(request.userName())) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(ApiResponse.error("Username already exists"));
        }

        updated = true;
      }

      if (request.newPassword() != null
          && !request.newPassword().trim().isEmpty()
          && !request.newPassword().equals(request.currentPassword())) {

//        userDto.password(request.newPassword());
        updated = true;
        logger.debug("Password will be updated for user: {}", email);
      }

      if (!updated) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("No changes detected"));
      }

      // Wykonaj aktualizację
//      userService.updateUser(userDto);

      // Pobierz zaktualizowane dane
     UserDto updatedUserDtoOptional = userService.findByEmail(email);


      UserProfileResponse response = mapToUserProfileResponse(updatedUserDtoOptional);

      logger.info("Profile updated successfully for user: {}", email);
      return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));

    } catch (Exception e) {
      logger.error(
          "Error updating profile for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to update profile"));
    }
  }

  @PostMapping("/verify-password")
  public ResponseEntity<ApiResponse<PasswordVerificationResponse>> verifyPassword(
      @Valid @RequestBody PasswordVerificationRequest request, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();
      boolean isValid = userService.verifyCurrentPassword(email, request.password());

      PasswordVerificationResponse response = new PasswordVerificationResponse(isValid);

      if (isValid) {
        logger.debug("Password verification successful for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success("Password verified", response));
      } else {
        logger.debug("Password verification failed for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success("Password verification completed", response));
      }

    } catch (Exception e) {
      logger.error(
          "Error verifying password for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to verify password"));
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<String>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();

      if (!userService.verifyCurrentPassword(email, request.currentPassword())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Current password is incorrect"));
      }

      if (request.currentPassword().equals(request.newPassword())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("New password must be different from current password"));
      }

//      UserDto userDto = new UserDto();
//      userDto.email(email);
//      userDto.password(request.newPassword());
//
//      userService.updateUser(userDto);

      logger.info("Password changed successfully for user: {}", email);
      return ResponseEntity.ok(
          ApiResponse.success("Password changed successfully", "Password updated"));

    } catch (Exception e) {
      logger.error(
          "Error changing password for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to change password"));
    }
  }

  @PostMapping("/change-username")
  public ResponseEntity<ApiResponse<UserProfileResponse>> changeUsername(
      @Valid @RequestBody ChangeUsernameRequest request, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();

      if (!userService.verifyCurrentPassword(email, request.currentPassword())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Current password is incorrect"));
      }

      UserDto currentUser = userService.findByEmail(email);

      if (request.newUsername().equals(currentUser.name())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("New username must be different from current username"));
      }

      if (userService.usernameExists(request.newUsername())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Username already exists"));
      }

//      UserDto userDto = new UserDto();
//      userDto.setEmail(email);
//      userDto.setUserName(request.newUsername());
//
//      userService.updateUser(userDto);

      UserDto updatedUserDtoOptional = userService.findByEmail(email);


      UserProfileResponse response = mapToUserProfileResponse(updatedUserDtoOptional);

      logger.info(
          "Username changed successfully for user: {} to: {}", email, request.newUsername());
      return ResponseEntity.ok(ApiResponse.success("Username changed successfully", response));

    } catch (Exception e) {
      logger.error(
          "Error changing username for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to change username"));
    }
  }

  private UserProfileResponse mapToUserProfileResponse(UserDto userDto) {
    return new UserProfileResponse(userDto.email(), userDto.name());
  }
}
