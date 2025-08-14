package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.message.ChangeRoleRequest;
import com.example.quizlecikprojekt.controllers.dto.message.CreateGroupRequest;
import com.example.quizlecikprojekt.controllers.dto.message.InviteCodeRequest;
import com.example.quizlecikprojekt.controllers.dto.message.UpdateGroupRequest;
import com.example.quizlecikprojekt.domain.friendship.SocialFacade;
import com.example.quizlecikprojekt.domain.friendship.entity.GroupMember;
import com.example.quizlecikprojekt.domain.friendship.entity.StudyGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('USER')")
public class StudyGroupController {

    @Autowired
    private SocialFacade socialFacade;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyGroups(Authentication authentication) {
        Map<String, Object> groupInfo = socialFacade.getUserGroupInfo(authentication.getName());
        return ResponseEntity.ok(groupInfo);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGroup(
            @Valid @RequestBody CreateGroupRequest groupRequest,
            Authentication authentication) {

        StudyGroup group = socialFacade.createStudyGroup(
                authentication.getName(),
                groupRequest.getName(),
                groupRequest.getDescription(),
                groupRequest.getIsPrivate()
        );

        Map<String, Object> response = Map.of(
                "message", "Grupa została utworzona",
                "group", group
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Map<String, Object>> joinGroup(
            @PathVariable Long groupId,
            Authentication authentication) {

        GroupMember membership = socialFacade.joinStudyGroup(authentication.getName(), groupId);

        Map<String, Object> response = Map.of(
                "message", "Dołączyłeś do grupy",
                "membership", membership
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/join-by-code")
    public ResponseEntity<Map<String, Object>> joinByInviteCode(
            @Valid @RequestBody InviteCodeRequest inviteRequest,
            Authentication authentication) {

        GroupMember membership = socialFacade.joinGroupByInviteCode(
                authentication.getName(),
                inviteRequest.getInviteCode()
        );

        Map<String, Object> response = Map.of(
                "message", "Dołączyłeś do grupy",
                "membership", membership
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, String>> leaveGroup(
            @PathVariable Long groupId,
            Authentication authentication) {

        socialFacade.leaveStudyGroup(authentication.getName(), groupId);

        return ResponseEntity.ok(Map.of("message", "Opuściłeś grupę"));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroupDetails(
            @PathVariable Long groupId,
            Authentication authentication) {

        Map<String, Object> details = socialFacade.getGroupDetails(groupId, authentication.getName());
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupRequest updateRequest,
            Authentication authentication) {

        Map<String, Object> params = Map.of(
                "name", updateRequest.getName(),
                "description", updateRequest.getDescription(),
                "isPrivate", updateRequest.getIsPrivate(),
                "maxMembers", updateRequest.getMaxMembers()
        );

        Map<String, Object> result = socialFacade.manageGroup(
                authentication.getName(), groupId, "update", params
        );

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Map<String, String>> deleteGroup(
            @PathVariable Long groupId,
            Authentication authentication) {

        socialFacade.manageGroup(authentication.getName(), groupId, "delete", Map.of());

        return ResponseEntity.ok(Map.of("message", "Grupa została usunięta"));
    }

    @PostMapping("/{groupId}/regenerate-code")
    public ResponseEntity<Map<String, Object>> regenerateInviteCode(
            @PathVariable Long groupId,
            Authentication authentication) {

        Map<String, Object> result = socialFacade.manageGroup(
                authentication.getName(), groupId, "regenerate_code", Map.of()
        );

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            Authentication authentication) {

        Map<String, Object> params = Map.of("memberId", memberId);
        socialFacade.manageGroup(authentication.getName(), groupId, "remove_member", params);

        return ResponseEntity.ok(Map.of("message", "Członek został usunięty"));
    }

    @PutMapping("/{groupId}/members/{memberId}/role")
    public ResponseEntity<Map<String, Object>> changeRole(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @Valid @RequestBody ChangeRoleRequest roleRequest,
            Authentication authentication) {

        Map<String, Object> params = Map.of(
                "memberId", memberId,
                "role", roleRequest.getRole()
        );

        Map<String, Object> result = socialFacade.manageGroup(
                authentication.getName(), groupId, "change_role", params
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchGroups(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> results = socialFacade.searchSocial(searchTerm, authentication.getName(), pageable);

        return ResponseEntity.ok(results);
    }
}