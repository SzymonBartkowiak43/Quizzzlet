package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.message.*;
import com.example.quizlecikprojekt.domain.friendship.SocialFacade;
import com.example.quizlecikprojekt.domain.friendship.entity.PrivateMessage;
import com.example.quizlecikprojekt.domain.friendship.entity.PrivateMessageDto;
import com.example.quizlecikprojekt.domain.group.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@PreAuthorize("hasRole('USER')")
@AllArgsConstructor
public class MessageController {

    private final SocialFacade socialFacade;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyMessages(Authentication authentication) {
        Map<String, Object> messagingInfo = socialFacade.getUserMessagingInfo(authentication.getName());
        return ResponseEntity.ok(messagingInfo);
    }

    @PostMapping("/private")
    public ResponseEntity<Map<String, Object>> sendPrivateMessage(
            @Valid @RequestBody SendPrivateMessageRequest messageRequest,
            Authentication authentication) {
        PrivateMessage message = socialFacade.sendPrivateMessage(
                authentication.getName(),
                messageRequest.getRecipientId(),
                messageRequest.getContent()
        );
        Map<String, Object> response = Map.of(
                "message", "Wiadomość została wysłana",
                "sentMessage", toDto(message)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/private/conversation/{userId}")
    public ResponseEntity<Map<String, Object>> getConversation(
            @PathVariable Long userId,
            Authentication authentication) {

        Map<String, Object> result = socialFacade.manageConversation(
                authentication.getName(), userId, "get_messages", Map.of()
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/group")
    public ResponseEntity<Map<String, Object>> sendGroupMessage(
            @Valid @RequestBody SendGroupMessageRequest messageRequest,
            Authentication authentication) {

        GroupMessageDto message = socialFacade.sendGroupMessage(
                authentication.getName(),
                messageRequest.getGroupId(),
                messageRequest.getContent()
        );

        Map<String, Object> response = Map.of(
                "message", "Wiadomość została wysłana do grupy",
                "sentMessage", message
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/groups")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupRequest req, Authentication auth) {
        GroupDto group = socialFacade.createGroup(auth.getName(), req.getName(), req.getMemberIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping("/groups/my")
    public ResponseEntity<?> getMyGroups(Authentication auth) {
        List<GroupDto> groups = socialFacade.getGroupsForUser(auth.getName());
        return ResponseEntity.ok(Map.of("groups", groups));
    }

    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable Long groupId, Authentication auth) {
        List<GroupMessageDto> messages = socialFacade.getGroupMessages(auth.getName(), groupId);
        return ResponseEntity.ok(Map.of("messages", messages));
    }

    public PrivateMessageDto toDto(PrivateMessage message) {
        return new PrivateMessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getContent(),
                message.getCreatedAt().toString()
        );
    }

}