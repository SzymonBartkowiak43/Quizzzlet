package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.controllers.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFacade {

  private final UserService userService;
  private final UserRepository userRepository;

  public UserFacade(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

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
            .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
            .toList();
  }
}
