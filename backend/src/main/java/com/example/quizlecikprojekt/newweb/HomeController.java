package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.HomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {

  @GetMapping("/home")
  public ResponseEntity<ApiResponse<HomeResponse>> home() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isLoggedIn =
        authentication != null
            && authentication.isAuthenticated()
            && !authentication.getPrincipal().equals("anonymousUser");

    String username = isLoggedIn ? authentication.getName() : null;

    HomeResponse homeResponse = new HomeResponse(isLoggedIn, username);

    return ResponseEntity.ok(ApiResponse.success("Home data retrieved", homeResponse));
  }
}
