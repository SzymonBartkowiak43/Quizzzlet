package com.example.quizlecikprojekt.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Log4j2
@Component
@AllArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

  private final JwtConfigurationProperties properties;

  @Override
  protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String authorization = request.getHeader("Authorization");
    if (authorization == null) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              getUsernamePasswordAuthenticationToken(authorization);
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    } catch (Exception e) {
      log.error("Error processing JWT token: {}", e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token) {
    String secretKey = properties.secret();
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT jwt = verifier.verify(token.substring(7));
    return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, Collections.emptyList());
  }
}
