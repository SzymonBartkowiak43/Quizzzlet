package com.example.quizlecikprojekt.controllers.loginandregister;

import com.example.quizlecikprojekt.config.security.JwtAuthenticatorFacade;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.JwtResponseDto;
import com.example.quizlecikprojekt.controllers.loginandregister.dto.TokenRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://68.183.66.208:3000")
public class TokenController {

  private final JwtAuthenticatorFacade jwtAuthenticatorFacade;

  @PostMapping("/token")
  public ResponseEntity<JwtResponseDto> authericateAndGenerateToken(
      @RequestBody TokenRequestDto tokenRequest) {
    final JwtResponseDto jwtResponse =
        jwtAuthenticatorFacade.authenticateAndGenerateToken(tokenRequest);
    return ResponseEntity.ok(jwtResponse);
  }
}
