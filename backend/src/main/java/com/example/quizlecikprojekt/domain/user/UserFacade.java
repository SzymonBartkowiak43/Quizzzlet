package com.example.quizlecikprojekt.domain.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserFacade {

  private final UserService userService;

  public User getUserByEmail(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    if (user == null) {
      throw new EntityNotFoundException("User with email " + userEmail + " not found");
    }
    return user;
  }
}
