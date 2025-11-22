package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.controllers.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.domain.user.dto.UserResponseDto;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserFacade {

  private final UserService userService;
  private final UserRepository userRepository;

  public User getUserByEmail(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    if (user == null) {
      throw new EntityNotFoundException("User with email " + userEmail + " not found");
    }
    return user;
  }

  public List<UserDto> getAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
  }

  public User getUserById(Long userId) {
    return userService.getUserById(userId);
  }

  public Object findAllById(List<Long> memberIds) {
    return userRepository.findAllById(memberIds);
  }

  public UserResponseDto createNewUser(UserRegisterDto userRegisterDto) {
    return userService.createNewUser(userRegisterDto);
  }

  public void deleteUser(Long userId) {
    userService.deleteUser(userId);
  }

  private UserDto toDto(User user) {
    List<String> roles = user.getRoles().stream()
            .map(UserRole::getName)
            .toList();

    return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            roles
    );
  }

}
