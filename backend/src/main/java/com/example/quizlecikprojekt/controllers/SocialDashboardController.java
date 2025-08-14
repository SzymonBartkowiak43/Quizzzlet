package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.friendship.SocialFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/social")
@PreAuthorize("hasRole('USER')")
public class SocialDashboardController {

    @Autowired
    private SocialFacade socialFacade;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getUserDashboard(Authentication authentication) {
        Map<String, Object> dashboard = socialFacade.getUserSocialDashboard(authentication.getName());
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSocial(
            @RequestParam(required = false) String searchTerm,
            Pageable pageable,
            Authentication authentication) {

        Map<String, Object> results = socialFacade.searchSocial(searchTerm, authentication.getName(), pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSocialStats(Authentication authentication) {
        Map<String, Object> dashboard = socialFacade.getUserSocialDashboard(authentication.getName());
        return ResponseEntity.ok((Map<String, Object>) dashboard.get("stats"));
    }
}
