package com.example.quizlecikprojekt.domain.friendship;

import com.example.quizlecikprojekt.domain.friendship.entity.*;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import com.example.quizlecikprojekt.domain.friendship.service.FriendshipService;
import com.example.quizlecikprojekt.domain.friendship.service.MessageService;
import com.example.quizlecikprojekt.domain.friendship.service.StudyGroupService;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class SocialFacade {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private StudyGroupService studyGroupService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    // ========== FRIENDSHIP OPERATIONS ==========

    /**
     * Wyślij zaproszenie do przyjaźni
     */
    public Friendship sendFriendRequest(String userEmail, Long addresseeId) {
        Long requesterId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        return friendshipService.sendFriendRequest(requesterId, addresseeId);
    }

    /**
     * Zaakceptuj zaproszenie do przyjaźni
     */
    public Friendship acceptFriendRequest(String userEmail, Long friendshipId) {
        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        return friendshipService.acceptFriendRequest(userId, friendshipId);
    }

    /**
     * Odrzuć zaproszenie do przyjaźni
     */
    public void declineFriendRequest(String userEmail, Long friendshipId) {
        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        friendshipService.declineFriendRequest(userId, friendshipId);
    }

    /**
     * Usuń przyjaźń
     */
    public void removeFriend(String userEmail, Long friendId) {
        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        friendshipService.removeFriend(userId, friendId);
    }

    /**
     * Zablokuj użytkownika
     */
    public void blockUser(String userEmail, Long userToBlockId) {
        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        friendshipService.blockUser(userId, userToBlockId);
    }

    /**
     * Pobierz kompletne informacje o przyjaciołach użytkownika
     */
    public Map<String, Object> getUserFriendshipInfo(String userName) {
        Long userId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        Map<String, Object> friendshipInfo = new HashMap<>();

        // Przyjaciele
        List<FriendDto> friends = friendshipService.getUserFriends(userId);
        friendshipInfo.put("friends", friends);
        friendshipInfo.put("friendsCount", friends.size());

        // Oczekujące zaproszenia
        List<Friendship> pendingRequests = friendshipService.getPendingFriendRequests(userId);
        friendshipInfo.put("pendingRequests", pendingRequests);
        friendshipInfo.put("pendingRequestsCount", pendingRequests.size());

        // Wysłane zaproszenia
        List<Friendship> sentRequests = friendshipService.getSentFriendRequests(userId);
        friendshipInfo.put("sentRequests", sentRequests);
        friendshipInfo.put("sentRequestsCount", sentRequests.size());

        // Sugerowani znajomi
        List<User> suggestedFriends = friendshipService.getSuggestedFriends(userId);
        friendshipInfo.put("suggestedFriends", suggestedFriends);

        return friendshipInfo;
    }

    /**
     * Sprawdź status przyjaźni między dwoma użytkownikami
     */
    public Map<String, Object> checkFriendshipStatus(String userName, Long userId2) {
        Long userId1 = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        Map<String, Object> status = new HashMap<>();

        boolean areFriends = friendshipService.areUsersFriends(userId1, userId2);
        FriendshipStatus friendshipStatus = friendshipService.getFriendshipStatus(userId1, userId2);

        status.put("areFriends", areFriends);
        status.put("status", friendshipStatus);
        status.put("canSendRequest", friendshipStatus == null);
        status.put("hasPendingRequest", friendshipStatus == FriendshipStatus.PENDING);

        return status;
    }

    // ========== GROUP OPERATIONS ==========

    /**
     * Utwórz nową grupę nauki
     */
    public StudyGroup createStudyGroup(String userName, String name, String description, Boolean isPrivate) {
        Long creatorId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        return studyGroupService.createGroup(creatorId, name, description, isPrivate);
    }

    /**
     * Dołącz do grupy
     */
    public GroupMember joinStudyGroup(String userName, Long groupId) {
        Long userId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        return studyGroupService.joinGroup(userId, groupId);
    }

    /**
     * Dołącz do grupy przez kod zaproszenia
     */
    public GroupMember joinGroupByInviteCode(String userName, String inviteCode) {
        Long userId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        return studyGroupService.joinGroupByInviteCode(userId, inviteCode);
    }

    /**
     * Opuść grupę
     */
    public void leaveStudyGroup(String userName, Long groupId) {
        Long userId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        studyGroupService.leaveGroup(userId, groupId);
    }

    /**
     * Pobierz kompletne informacje o grupach użytkownika
     */
    public Map<String, Object> getUserGroupInfo(String userEmail) {

        Long userId = userRepository.getUserByEmail(userEmail).get().getId();
        Map<String, Object> groupInfo = new HashMap<>();

        // Grupy członkowskie
        List<StudyGroup> userGroups = studyGroupService.getUserGroups(userId);
        groupInfo.put("memberGroups", userGroups);
        groupInfo.put("memberGroupsCount", userGroups.size());

        // Utworzone grupy
        List<StudyGroup> createdGroups = userGroups.stream()
                .filter(group -> group.getCreator().getId().equals(userId))
                .toList();
        groupInfo.put("createdGroups", createdGroups);
        groupInfo.put("createdGroupsCount", createdGroups.size());

        // Grupy z najnowszą aktywnością
        groupInfo.put("activeGroups", userGroups.stream()
                .filter(group -> !group.getMessages().isEmpty())
                .toList());

        return groupInfo;
    }

    /**
     * Zarządzanie grupą - kompletne operacje
     */
    public Map<String, Object> manageGroup(String userName, Long groupId, String action, Map<String, Object> params) {

        Long userId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();


        Map<String, Object> result = new HashMap<>();

        switch (action.toLowerCase()) {
            case "update":
                StudyGroup updatedGroup = studyGroupService.updateGroup(
                        userId, groupId,
                        (String) params.get("name"),
                        (String) params.get("description"),
                        (Boolean) params.get("isPrivate"),
                        (Integer) params.get("maxMembers")
                );
                result.put("group", updatedGroup);
                result.put("message", "Grupa została zaktualizowana");
                break;

            case "delete":
                studyGroupService.deleteGroup(userId, groupId);
                result.put("message", "Grupa została usunięta");
                break;

            case "regenerate_code":
                StudyGroup groupWithNewCode = studyGroupService.regenerateInviteCode(userId, groupId);
                result.put("newInviteCode", groupWithNewCode.getInviteCode());
                result.put("message", "Kod zaproszenia został wygenerowany ponownie");
                break;

            case "remove_member":
                Long memberToRemoveId = ((Number) params.get("memberId")).longValue();
                studyGroupService.removeMember(userId, groupId, memberToRemoveId);
                result.put("message", "Członek został usunięty z grupy");
                break;

            case "change_role":
                Long memberId = ((Number) params.get("memberId")).longValue();
                GroupRole newRole = GroupRole.valueOf((String) params.get("role"));
                GroupMember updatedMember = studyGroupService.changeRole(userId, groupId, memberId, newRole);
                result.put("member", updatedMember);
                result.put("message", "Rola została zmieniona");
                break;

            default:
                throw new InvalidOperationException("Nieznana operacja: " + action);
        }

        return result;
    }

    /**
     * Pobierz szczegóły grupy z członkami
     */
    public Map<String, Object> getGroupDetails(Long groupId, String userName) {

        Long requestingUserId = userRepository.getUserByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        StudyGroup group = studyGroupService.getGroupById(groupId);

        if (group.getIsPrivate() && !studyGroupService.isUserMember(requestingUserId, groupId)) {
            throw new InvalidOperationException("Nie masz dostępu do tej grupy");
        }

        Map<String, Object> details = new HashMap<>();
        details.put("group", group);
        details.put("members", studyGroupService.getGroupMembers(groupId));
        details.put("memberCount", group.getMemberCount());
        details.put("isUserMember", studyGroupService.isUserMember(requestingUserId, groupId));
        details.put("recentMessages", messageService.getGroupMessages(groupId));

        return details;
    }

    // ========== MESSAGING OPERATIONS ==========

    /**
     * Wyślij prywatną wiadomość
     */
    public PrivateMessage sendPrivateMessage(String userEmail, Long recipientId, String content) {
        Long senderId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        return messageService.sendPrivateMessage(senderId, recipientId, content);
    }

    /**
     * Wyślij zestaw słówek prywatnie
     */
    public PrivateMessage shareWordSetPrivately(String userEmail, Long recipientId, String message, Long wordSetId) {
        Long senderId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
        return messageService.sendWordSet(senderId, recipientId, message, wordSetId);
    }

    /**
     * Wyślij wiadomość do grupy
     */
    public GroupMessage sendGroupMessage(String userEmail, Long groupId, String content) {
        Long senderId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        return messageService.sendGroupMessage(senderId, groupId, content);
    }

    /**
     * Udostępnij zestaw słówek w grupie
     */
    public GroupMessage shareWordSetInGroup(String userEmail, Long groupId, String message, Long wordSetId) {
        Long senderId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        return messageService.sendWordSetToGroup(senderId, groupId, message, wordSetId);
    }

    /**
     * Pobierz kompletne informacje o wiadomościach użytkownika
     */
    public Map<String, Object> getUserMessagingInfo(String userEmail) {
        Map<String, Object> messagingInfo = new HashMap<>();

        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        // Prywatne konwersacje
        List<PrivateMessage> conversations = messageService.getUserConversations(userId);
        messagingInfo.put("conversations", conversations);
        messagingInfo.put("conversationsCount", conversations.size());

        // Nieprzeczytane wiadomości
        List<PrivateMessage> unreadMessages = messageService.getUnreadMessages(userId);
        messagingInfo.put("unreadMessages", unreadMessages);
        messagingInfo.put("unreadCount", messageService.getUnreadMessageCount(userId));

        // Ostatnie aktywne grupy (z wiadomościami)
        List<StudyGroup> userGroups = studyGroupService.getUserGroups(userId);
        List<StudyGroup> activeGroups = userGroups.stream()
                .filter(group -> !group.getMessages().isEmpty())
                .limit(5)
                .toList();
        messagingInfo.put("activeGroups", activeGroups);

        return messagingInfo;
    }

    /**
     * Zarządzanie konwersacją
     */
    public Map<String, Object> manageConversation(String userEmail, Long otherUserId, String action, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        switch (action.toLowerCase()) {
            case "get_messages":
                List<PrivateMessage> messages = messageService.getConversation(userId, otherUserId);
                result.put("messages", messages);
                break;

            case "mark_as_read":
                int markedCount = messageService.markMessagesAsRead(userId, otherUserId);
                result.put("markedCount", markedCount);
                result.put("message", "Wiadomości zostały oznaczone jako przeczytane");
                break;

            case "delete_message":
                Long messageId = ((Number) params.get("messageId")).longValue();
                messageService.deletePrivateMessage(userId, messageId);
                result.put("message", "Wiadomość została usunięta");
                break;

            default:
                throw new InvalidOperationException("Nieznana operacja: " + action);
        }

        return result;
    }

    // ========== SEARCH AND DISCOVERY ==========

    /**
     * Wyszukaj użytkowników, grupy i zawartość
     */
    public Map<String, Object> searchSocial(String searchTerm, String requestUserName, Pageable pageable) {
        Map<String, Object> searchResults = new HashMap<>();

        Long requestingUserId = userRepository.getUserByEmail(requestUserName)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        // Wyszukaj grupy
        Page<StudyGroup> groups = studyGroupService.searchGroups(searchTerm, requestingUserId, pageable);
        searchResults.put("groups", groups);

        // Sugerowani znajomi (jeśli brak search term)
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            List<User> suggestedFriends = friendshipService.getSuggestedFriends(requestingUserId);
            searchResults.put("suggestedFriends", suggestedFriends);
        }

        return searchResults;
    }

    /**
     * Pobierz dashboard użytkownika - kompletny przegląd aktywności społecznościowej
     */
    private Long getUserIdFromUsername(String username) {
        return userRepository.getUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();
    }

    public Map<String, Object> getUserSocialDashboard(String username) {
        Long userId = getUserIdFromUsername(username);
        Map<String, Object> dashboard = new HashMap<>();

        // Podstawowe informacje
        dashboard.put("friendshipInfo", getUserFriendshipInfo(username));
        dashboard.put("groupInfo", getUserGroupInfo(username));
        dashboard.put("messagingInfo", getUserMessagingInfo(username));

        // Statystyki
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFriends", friendshipService.getUserFriends(userId).size());
        stats.put("totalGroups", studyGroupService.getUserGroups(userId).size());
        stats.put("unreadMessages", messageService.getUnreadMessageCount(userId));
        stats.put("pendingFriendRequests", friendshipService.getPendingFriendRequests(userId).size());

        dashboard.put("stats", stats);

        // Szybkie akcje
        Map<String, Object> quickActions = new HashMap<>();
        quickActions.put("canCreateGroup", true);
        quickActions.put("hasUnreadMessages", messageService.getUnreadMessageCount(userId) > 0);
        quickActions.put("hasPendingRequests", friendshipService.getPendingFriendRequests(userId).size() > 0);

        dashboard.put("quickActions", quickActions);

        return dashboard;
    }


    // ========== BULK OPERATIONS ==========

    /**
     * Operacje hurtowe na przyjaciołach
     */
    @Transactional
    public Map<String, Object> bulkFriendshipOperations(String userEmail, String operation, List<Long> targetUserIds) {
        Long userId = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"))
                .getId();

        Map<String, Object> result = new HashMap<>();
        List<String> successIds = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();

        for (Long targetId : targetUserIds) {
            try {
                switch (operation.toLowerCase()) {
                    case "send_requests":
                        friendshipService.sendFriendRequest(userId, targetId);
                        successIds.add(targetId.toString());
                        break;
                    case "remove_friends":
                        friendshipService.removeFriend(userId, targetId);
                        successIds.add(targetId.toString());
                        break;
                    default:
                        throw new InvalidOperationException("Nieznana operacja: " + operation);
                }
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("userId", targetId.toString());
                error.put("error", e.getMessage());
                errors.add(error);
            }
        }

        result.put("successful", successIds);
        result.put("errors", errors);
        result.put("successCount", successIds.size());
        result.put("errorCount", errors.size());

        return result;
    }
}