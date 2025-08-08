package com.example.quizlecikprojekt.config.security;

import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;

@AllArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String email) throws BadCredentialsException {
    UserDto userDto = userService.getUserByEmail(email);
    return getUser(userDto);
  }

  private User getUser(UserDto user) {
    return new User(
        user.email(),
        user.password(),
        user.roles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .toList());
  }
}
