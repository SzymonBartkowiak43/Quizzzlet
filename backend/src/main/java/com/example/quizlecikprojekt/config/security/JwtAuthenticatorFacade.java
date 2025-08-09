package com.example.quizlecikprojekt.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.JwtResponseDto;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.TokenRequestDto;
import java.time.*;
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

  public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto tokenRequest) {
    Authentication authenticate =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(tokenRequest.email(), tokenRequest.password()));

    User user = (User) authenticate.getPrincipal();
    String token = createToken(user);
    String email = user.getUsername();
    return JwtResponseDto.builder().token(token).email(email).build();
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
