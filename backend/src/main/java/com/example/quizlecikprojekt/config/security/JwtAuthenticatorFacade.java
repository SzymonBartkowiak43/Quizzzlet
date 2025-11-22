package com.example.quizlecikprojekt.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.JwtResponseDto;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.TokenRequestDto;


import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.entity.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JwtAuthenticatorFacade {

  private final AuthenticationManager authenticationManager;
  private final JwtConfigurationProperties properties;
  private final Clock clock;
  private final UserRepository userRepository;

  public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto tokenRequest) {
    Authentication authenticate =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(tokenRequest.email(), tokenRequest.password()));

    User principal = (User) authenticate.getPrincipal();

    String token = createToken(principal);

    com.example.quizlecikprojekt.entity.User domainUser = userRepository.getUserByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found in database"));

    List<String> roles = domainUser.getRoles().stream()
            .map(UserRole::getName)
            .collect(Collectors.toList());

    JwtResponseDto.UserSummary userSummary = new JwtResponseDto.UserSummary(
            domainUser.getId(),
            domainUser.getEmail(),
            domainUser.getName(),
            roles
    );

    return JwtResponseDto.builder()
            .token(token)
            .user(userSummary)
            .build();
  }

  private String createToken(User user) {
    String secretKey = properties.secret();
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
    Instant expiresAt = now.plus(Duration.ofDays(properties.expirationDays()));
    String issuer = properties.issuer();
    return JWT.create()
            .withSubject(user.getUsername())
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .withIssuer(issuer)
            .sign(algorithm);
  }
}