package com.example.quizlecikprojekt.config.security;

import com.example.quizlecikprojekt.domain.user.UserFacade;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@AllArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

  private final UserFacade userFacade;

  @Override
  public UserDetails loadUserByUsername(String email) throws BadCredentialsException {
    return getUser(userFacade.getUserByEmail(email));
  }

  private User getUser(com.example.quizlecikprojekt.entity.User user) {
    return new User(
        user.getEmail(),
        user.getPassword(),
        user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList());
  }
}
