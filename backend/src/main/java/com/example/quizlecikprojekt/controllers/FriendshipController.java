package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.message.BulkFriendshipRequest;
import com.example.quizlecikprojekt.controllers.dto.message.FriendRequestDto;
import com.example.quizlecikprojekt.domain.friendship.SocialFacade;
import com.example.quizlecikprojekt.entity.Friendship;
import com.example.quizlecikprojekt.domain.friendship.dto.FriendshipDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/friendships")
@AllArgsConstructor
public class FriendshipController {

    private final SocialFacade socialFacade;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyFriendships(Authentication authentication) {
        Map<String, Object> friendshipInfo = socialFacade.getUserFriendshipInfo(authentication.getName());
        return ResponseEntity.ok(friendshipInfo);
    }

    @PostMapping("/send-request")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @Valid @RequestBody FriendRequestDto requestDto,
            Authentication authentication) {

        FriendshipDto friendship = socialFacade.sendFriendRequest(
                authentication.getName(),
                requestDto.getAddresseeId()
        );

        Map<String, Object> response = Map.of(
                "message", "Zaproszenie do przyjaźni zostało wysłane",
                "friendship", friendship
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{friendshipId}/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(
            @PathVariable Long friendshipId,
            Authentication authentication) {

        Friendship friendship = socialFacade.acceptFriendRequest(authentication.getName(), friendshipId);

        Map<String, Object> response = Map.of(
                "message", "Zaproszenie zostało zaakceptowane",
                "friendship", friendship
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{friendshipId}/decline")
    public ResponseEntity<Map<String, String>> declineFriendRequest(
            @PathVariable Long friendshipId,
            Authentication authentication) {

        socialFacade.declineFriendRequest(authentication.getName(), friendshipId);

        return ResponseEntity.ok(Map.of("message", "Zaproszenie zostało odrzucone"));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Map<String, String>> removeFriend(
            @PathVariable Long friendId,
            Authentication authentication) {

        socialFacade.removeFriend(authentication.getName(), friendId);

        return ResponseEntity.ok(Map.of("message", "Przyjaźń została usunięta"));
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable Long userId,
            Authentication authentication) {

        socialFacade.blockUser(authentication.getName(), userId);

        return ResponseEntity.ok(Map.of("message", "Użytkownik został zablokowany"));
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> checkFriendshipStatus(
            @PathVariable Long userId,
            Authentication authentication) {

        Map<String, Object> status = socialFacade.checkFriendshipStatus(
                authentication.getName(), userId
        );

        return ResponseEntity.ok(status);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkFriendshipOperations(
            @Valid @RequestBody BulkFriendshipRequest bulkRequest,
            Authentication authentication) {

        Map<String, Object> result = socialFacade.bulkFriendshipOperations(
                authentication.getName(),
                bulkRequest.getOperation(),
                bulkRequest.getTargetUserIds()
        );

        return ResponseEntity.ok(result);
    }
}