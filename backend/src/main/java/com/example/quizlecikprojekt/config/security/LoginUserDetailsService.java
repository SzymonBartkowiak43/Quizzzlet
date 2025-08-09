package com.example.quizlecikprojekt.config.security;

import com.example.quizlecikprojekt.domain.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@AllArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String email) throws BadCredentialsException {
    return getUser(userService.getUserByEmail(email));
  }

  private User getUser(com.example.quizlecikprojekt.domain.user.User user) {
    return new User(
        user.getEmail(),
        user.getPassword(),
        user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList());
  }
}
