package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import com.example.quizlecikprojekt.domain.user.dto.UserResponseDto;
import com.example.quizlecikprojekt.domain.user.exception.UserAlreadyExistsException;
import com.example.quizlecikprojekt.domain.user.exception.UserNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private static final String DEFAULT_USER_ROLE = "USER";

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository userRepository,
      UserRoleRepository userRoleRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
  }

  public User getUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
  }

  public Long getUserIdByUsername(String username) {
    return userRepository
        .findByUserName(username)
        .map(User::getId)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
  }

  public Optional<UserDto> findCredentialsByEmail(String email) {
    return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::map);
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUserName(username);
  }

  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public boolean usernameExists(String username) {
    return userRepository.findByUserName(username).isPresent();
  }

  @Transactional
  public UserResponseDto registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {
    if (emailExists(userRegistrationDto.email())) {
      throw new UserAlreadyExistsException("Email already exists: " + userRegistrationDto.email());
    }

    if (usernameExists(userRegistrationDto.username())) {
      throw new UserAlreadyExistsException(
          "Username already exists: " + userRegistrationDto.username());
    }

    User user = new User();
    user.setEmail(userRegistrationDto.email());
    user.setUserName(userRegistrationDto.username());
    user.setPassword(passwordEncoder.encode(userRegistrationDto.password()));

    UserRole defaultRole =
        userRoleRepository
            .findByName(DEFAULT_USER_ROLE)
            .orElseThrow(
                () -> new IllegalStateException("Default role not found: " + DEFAULT_USER_ROLE));

    user.getRoles().add(defaultRole);

    User savedUser = userRepository.save(user);

    Set<String> roleNames =
        savedUser.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet());

    return new UserResponseDto(
        savedUser.getId(), savedUser.getEmail(), savedUser.getUserName(), roleNames);
  }

  @Transactional
  public void updateUser(UserDto userDto) {
    User user =
        userRepository
            .findByEmail(userDto.getEmail())
            .orElseThrow(
                () ->
                    new UserNotFoundException("User not found with email: " + userDto.getEmail()));

    if (userDto.getPassword() != null
        && !userDto.getPassword().trim().isEmpty()
        && !passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(userDto.getPassword()));
      logger.debug("Password updated for user: {}", user.getEmail());
    }

    if (userDto.getUserName() != null
        && !userDto.getUserName().trim().isEmpty()
        && !userDto.getUserName().equals(user.getUserName())) {

      if (usernameExists(userDto.getUserName())) {
        throw new UserAlreadyExistsException("Username already exists: " + userDto.getUserName());
      }

      user.setUserName(userDto.getUserName());
      logger.debug("Username updated for user: {} to: {}", user.getEmail(), userDto.getUserName());
    }
  }

  public boolean verifyCurrentPassword(String email, String currentPassword) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

    boolean isValid = passwordEncoder.matches(currentPassword, user.getPassword());
    logger.debug("Password verification for user {}: {}", email, isValid ? "successful" : "failed");

    return isValid;
  }
}
