package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.domain.user.dto.UserResponseDto;
import com.example.quizlecikprojekt.exception.UserNotFoundException;
import com.example.quizlecikprojekt.domain.user.validator.PasswordValidator;
import java.util.Set;

import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserService {

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

  public UserResponseDto createNewUser(UserRegisterDto userDto) {
    PasswordValidator.validate(userDto.password());

    String encoded = passwordEncoder.encode(userDto.password());

    User newUser =
        createUser(new UserRegisterDto(userDto.email().trim(), userDto.name().trim(), encoded));

    Set<String> roleNames =
        newUser.getRoles().stream()
            .map(UserRole::getName)
            .collect(java.util.stream.Collectors.toSet());

    return new UserResponseDto(newUser.getId(), newUser.getEmail(), newUser.getName(), roleNames);
  }

  public User getUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
  }

  public Long getUserIdByUsername(String username) {
    return userRepository
        .getUserByName(username)
        .map(User::getId)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
  }

  public User getUserByEmail(String email) {
    return userRepository
        .getUserByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("Not found"));
  }

  private User createUser(UserRegisterDto userDto) {
    User user =
        User.builder()
            .name(userDto.name())
            .email(userDto.email())
            .password(userDto.password())
            .build();

    if (user.getRoles() == null) {
      user.setRoles(new java.util.HashSet<>());
    }
    user.addUserRole(getDefaultRole());

    return userRepository.save(user);
  }

  private UserRole getDefaultRole() {
    return userRoleRepository
        .findByName(DEFAULT_USER_ROLE)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Default role '"
                        + DEFAULT_USER_ROLE
                        + "' is missing. Seed it before running the app/tests."));
  }
}
