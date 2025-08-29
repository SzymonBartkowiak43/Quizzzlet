package com.example.quizlecikprojekt.domain.friendship;

import com.example.quizlecikprojekt.controllers.dto.UserDto;
import com.example.quizlecikprojekt.domain.friendship.entity.*;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import com.example.quizlecikprojekt.domain.friendship.service.FriendshipService;
import com.example.quizlecikprojekt.domain.friendship.service.MessageService;
import com.example.quizlecikprojekt.domain.group.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SocialFacade {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    // ========== FRIENDSHIP OPERATIONS ==========

    /**
     * Wyślij zaproszenie do przyjaźni
     */
    public FriendshipDto sendFriendRequest(String userEmail, Long addresseeId) {
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
        List<FriendshipDto> pendingRequests = friendshipService.getPendingFriendRequests(userId)
                .stream()
                .map(SocialFacade::fromEntity)
                .collect(Collectors.toList());
        friendshipInfo.put("pendingRequests", pendingRequests);

        // Wysłane zaproszenia
        List<FriendshipDto> sentRequests = friendshipService.getSentFriendRequests(userId)
                .stream()
                .map(SocialFacade::fromEntity)
                .collect(Collectors.toList());
        friendshipInfo.put("sentRequests", sentRequests);
        friendshipInfo.put("sentRequestsCount", sentRequests.size());

        // Sugerowani znajomi
        List<UserDto> suggestedFriends = friendshipService.getSuggestedFriends(userId)
                        .stream()
                        .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
                        .collect(Collectors.toList());
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

//        // Ostatnie aktywne grupy (z wiadomościami)
//        List<StudyGroup> userGroups = studyGroupService.getUserGroups(userId);
//        List<StudyGroup> activeGroups = userGroups.stream()
//                .filter(group -> !group.getMessages().isEmpty())
//                .limit(5)
//                .toList();
//        messagingInfo.put("activeGroups", activeGroups);

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
                // KONWERSJA NA DTO!
                List<PrivateMessageDto> messageDtos = messages.stream()
                        .map(SocialFacade::toDto)
                        .toList();
                result.put("messages", messageDtos);
                break;

            case "mark_as_read":
                int markedCount = messageService.markMessagesAsRead(userId, otherUserId);
                result.put("markedCount", markedCount);
                result.put("message", "Wiadomości zostały oznaczone jako przeczytane");
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

//        // Wyszukaj grupy
//        Page<StudyGroup> groups = studyGroupService.searchGroups(searchTerm, requestingUserId, pageable);
//        searchResults.put("groups", groups);

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
//        dashboard.put("groupInfo", getUserGroupInfo(username));
        dashboard.put("messagingInfo", getUserMessagingInfo(username));

        // Statystyki
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFriends", friendshipService.getUserFriends(userId).size());
//        stats.put("totalGroups", studyGroupService.getUserGroups(userId).size());
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


    public GroupDto createGroup(String creatorEmail, String name, List<Long> memberIds) {
        User creator = userRepository.getUserByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<User> members = new HashSet<>((Collection) userRepository.findAllById(memberIds));
        members.add(creator);
        Group group = new Group(name, creator, members);
        Group save = groupRepository.save(group);
        return new GroupDto(
                save.getId(),
                save.getName(),
                save.getCreator().getId(),
                save.getCreator().getName(),
                save.getMembers().stream().map(User::getId).toList(),
                save.getMembers().stream().map(User::getName).toList()
        );

    }

    public List<GroupDto> getGroupsForUser(String userEmail) {
        User user = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Group> groups = groupRepository.findByMembers_Id(user.getId());
        return groups.stream().map(g -> new GroupDto(
                g.getId(),
                g.getName(),
                g.getCreator().getId(),
                g.getCreator().getName(),
                g.getMembers().stream().map(User::getId).toList(),
                g.getMembers().stream().map(User::getName).toList()
        )).toList();
    }

    public GroupMessageDto sendGroupMessage(String senderEmail, Long groupId, String content) {
        User sender = userRepository.getUserByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().contains(sender)) {
            throw new RuntimeException("User is not a member of the group");
        }
        GroupMessage msg = new GroupMessage(group, sender, content);
        GroupMessage save = groupMessageRepository.save(msg);
        return new GroupMessageDto(
                save.getId(),
                groupId,
                sender.getId(),
                sender.getName(),
                save.getContent(),
                save.getCreatedAt().toString()
        );
    }

    public List<GroupMessageDto> getGroupMessages(String userEmail, Long groupId) {
        User user = userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().contains(user)) {
            throw new RuntimeException("User is not a member of the group");
        }
        List<GroupMessage> messages = groupMessageRepository.findByGroup_IdOrderByCreatedAtAsc(groupId);
        return messages.stream().map(m -> new GroupMessageDto(
                m.getId(),
                groupId,
                m.getSender().getId(),
                m.getSender().getName(),
                m.getContent(),
                m.getCreatedAt().toString()
        )).toList();
    }






    private static PrivateMessageDto toDto(PrivateMessage msg) {
        return new PrivateMessageDto(
                msg.getId(),
                msg.getSender().getId(),
                msg.getRecipient().getId(),
                msg.getContent(),
                msg.getCreatedAt().toString()
        );
    }

    private static FriendshipDto fromEntity(Friendship f) {
        return new FriendshipDto(
                f.getId(),
                f.getRequester().getId(),
                f.getRequester().getName(),
                f.getRequester().getEmail(),
                f.getAddressee().getId(),
                f.getAddressee().getName(),
                f.getAddressee().getEmail(),
                f.getStatus().name()
        );
    }
}