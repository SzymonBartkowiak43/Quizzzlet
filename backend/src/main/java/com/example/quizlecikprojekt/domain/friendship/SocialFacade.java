package com.example.quizlecikprojekt.domain.friendship;

import com.example.quizlecikprojekt.controllers.dto.UserDto;
import com.example.quizlecikprojekt.domain.friendship.dto.*;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.friendship.service.FriendshipService;
import com.example.quizlecikprojekt.domain.friendship.service.MessageService;
import com.example.quizlecikprojekt.domain.group.*;
import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Friendship;
import com.example.quizlecikprojekt.entity.Group;
import com.example.quizlecikprojekt.entity.GroupMessage;
import com.example.quizlecikprojekt.entity.PrivateMessage;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SocialFacade {

    private final FriendshipService friendshipService;

    private final MessageService messageService;

    private final UserFacade userFacade;

    private final GroupRepository groupRepository;

    private final GroupMessageRepository groupMessageRepository;

    public FriendshipDto sendFriendRequest(String userEmail, Long addresseeId) {
        Long requesterId = userFacade.getUserByEmail(userEmail)
                .getId();

        return friendshipService.sendFriendRequest(requesterId, addresseeId);
    }

    public Friendship acceptFriendRequest(String userEmail, Long friendshipId) {
        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();
        return friendshipService.acceptFriendRequest(userId, friendshipId);
    }

    public void declineFriendRequest(String userEmail, Long friendshipId) {
        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();
        friendshipService.declineFriendRequest(userId, friendshipId);
    }

    public void removeFriend(String userEmail, Long friendId) {
        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();
        friendshipService.removeFriend(userId, friendId);
    }

    public void blockUser(String userEmail, Long userToBlockId) {
        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();
        friendshipService.blockUser(userId, userToBlockId);
    }

    public Map<String, Object> getUserFriendshipInfo(String userName) {
        Long userId = userFacade.getUserByEmail(userName)
                .getId();

        Map<String, Object> friendshipInfo = new HashMap<>();

        List<FriendDto> friends = friendshipService.getUserFriends(userId);
        friendshipInfo.put("friends", friends);
        friendshipInfo.put("friendsCount", friends.size());

        List<FriendshipDto> pendingRequests = friendshipService.getPendingFriendRequests(userId)
                .stream()
                .map(SocialFacade::fromEntity)
                .collect(Collectors.toList());
        friendshipInfo.put("pendingRequests", pendingRequests);

        List<FriendshipDto> sentRequests = friendshipService.getSentFriendRequests(userId)
                .stream()
                .map(SocialFacade::fromEntity)
                .collect(Collectors.toList());
        friendshipInfo.put("sentRequests", sentRequests);
        friendshipInfo.put("sentRequestsCount", sentRequests.size());

        List<UserDto> suggestedFriends = friendshipService.getSuggestedFriends(userId)
                        .stream()
                        .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
                        .collect(Collectors.toList());
        friendshipInfo.put("suggestedFriends", suggestedFriends);

        return friendshipInfo;
    }

    public Map<String, Object> checkFriendshipStatus(String userName, Long userId2) {
        Long userId1 = userFacade.getUserByEmail(userName)
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

    public PrivateMessage sendPrivateMessage(String userEmail, Long recipientId, String content) {
        Long senderId = userFacade.getUserByEmail(userEmail).getId();
        return messageService.sendPrivateMessage(senderId, recipientId, content);
    }

    public Map<String, Object> getUserMessagingInfo(String userEmail) {
        Map<String, Object> messagingInfo = new HashMap<>();

        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();

        List<PrivateMessage> conversations = messageService.getUserConversations(userId);
        messagingInfo.put("conversations", conversations);
        messagingInfo.put("conversationsCount", conversations.size());

        List<PrivateMessage> unreadMessages = messageService.getUnreadMessages(userId);
        messagingInfo.put("unreadMessages", unreadMessages);
        messagingInfo.put("unreadCount", messageService.getUnreadMessageCount(userId));


        return messagingInfo;
    }

    public Map<String, Object> manageConversation(String userEmail, Long otherUserId, String action, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long userId = userFacade.getUserByEmail(userEmail)
                .getId();

        switch (action.toLowerCase()) {
            case "get_messages":
                List<PrivateMessage> messages = messageService.getConversation(userId, otherUserId);
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


    public Map<String, Object> searchSocial(String searchTerm, String requestUserName, Pageable pageable) {
        Map<String, Object> searchResults = new HashMap<>();

        Long requestingUserId = userFacade.getUserByEmail(requestUserName)
                .getId();


        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            List<User> suggestedFriends = friendshipService.getSuggestedFriends(requestingUserId);
            searchResults.put("suggestedFriends", suggestedFriends);
        }

        return searchResults;
    }

    private Long getUserIdFromUsername(String username) {
        return userFacade.getUserByEmail(username)
                .getId();
    }

    public Map<String, Object> getUserSocialDashboard(String username) {
        Long userId = getUserIdFromUsername(username);
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("friendshipInfo", getUserFriendshipInfo(username));
        dashboard.put("messagingInfo", getUserMessagingInfo(username));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFriends", friendshipService.getUserFriends(userId).size());
        stats.put("unreadMessages", messageService.getUnreadMessageCount(userId));
        stats.put("pendingFriendRequests", friendshipService.getPendingFriendRequests(userId).size());

        dashboard.put("stats", stats);

        Map<String, Object> quickActions = new HashMap<>();
        quickActions.put("canCreateGroup", true);
        quickActions.put("hasUnreadMessages", messageService.getUnreadMessageCount(userId) > 0);
        quickActions.put("hasPendingRequests", !friendshipService.getPendingFriendRequests(userId).isEmpty());

        dashboard.put("quickActions", quickActions);

        return dashboard;
    }


    @Transactional
    public Map<String, Object> bulkFriendshipOperations(String userEmail, String operation, List<Long> targetUserIds) {
        Long userId = userFacade.getUserByEmail(userEmail)
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
        User creator = userFacade.getUserByEmail(creatorEmail);
        Set<User> members = new HashSet<>((Collection) userFacade.findAllById(memberIds));
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
        User user = userFacade.getUserByEmail(userEmail);
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
        User sender = userFacade.getUserByEmail(senderEmail);
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
        User user = userFacade.getUserByEmail(userEmail);
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