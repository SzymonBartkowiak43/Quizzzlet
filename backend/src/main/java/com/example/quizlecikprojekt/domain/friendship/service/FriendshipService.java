package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.friendship.dto.FriendDto;
import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.entity.Friendship;
import com.example.quizlecikprojekt.domain.friendship.dto.FriendshipDto;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.friendship.repository.FriendshipRepository;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    private final UserFacade userFacade;

    public FriendshipDto sendFriendRequest(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new InvalidOperationException("You can't invite yourself into friendship");
        }

        User requester = userFacade.getUserById(requesterId);
        User addressee = userFacade.getUserById(addresseeId);

        Optional<Friendship> existingFriendship = friendshipRepository
                .findFriendshipBetweenUsers(requesterId, addresseeId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new InvalidOperationException("Już jesteście przyjaciółmi");
            } else if (friendship.getStatus() == FriendshipStatus.PENDING) {
                throw new InvalidOperationException("You are already friends");
            } else if (friendship.getStatus() == FriendshipStatus.BLOCKED) {
                throw new InvalidOperationException("You cannot invite this user");
            }
        }

        Friendship newFriendship = new Friendship(requester, addressee);
        Friendship save = friendshipRepository.save(newFriendship);
        return fromEntity(save);
    }

    public Friendship acceptFriendRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new InvalidOperationException("You cannot accept this invitation");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("The invitation is not active");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    public void declineFriendRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new InvalidOperationException("You can't refuse this invitation");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository
                .findFriendshipBetweenUsers(userId, friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        if (!friendship.involves(userFacade.getUserById(userId))) {
            throw new InvalidOperationException("You can't delete this friendship");
        }

        friendshipRepository.delete(friendship);
    }

    public void blockUser(Long userId, Long userToBlockId) {
        if (userId.equals(userToBlockId)) {
            throw new InvalidOperationException("You can't block yourself");
        }

        User user = userFacade.getUserById(userId);
        User userToBlock = userFacade.getUserById(userToBlockId);

        Optional<Friendship> existingFriendship = friendshipRepository
                .findFriendshipBetweenUsers(userId, userToBlockId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            friendship.setStatus(FriendshipStatus.BLOCKED);
            friendshipRepository.save(friendship);
        } else {
            Friendship blockFriendship = new Friendship(user, userToBlock);
            blockFriendship.setStatus(FriendshipStatus.BLOCKED);
            friendshipRepository.save(blockFriendship);
        }
    }

    public List<FriendDto> getUserFriends(Long userId) {
        List<User> users = friendshipRepository.findRequestedFriends(userId);
        users.addAll(friendshipRepository.findAddedFriends(userId));
        return users.stream()
                .map(u -> new FriendDto(u.getId(), u.getEmail(), u.getName()))
                .collect(Collectors.toList());
    }

    public List<Friendship> getPendingFriendRequests(Long userId) {
        return friendshipRepository.findPendingFriendRequestsForUser(userId);
    }

    public List<Friendship> getSentFriendRequests(Long userId) {
        return friendshipRepository.findSentFriendRequestsByUser(userId);
    }

    public boolean areUsersFriends(Long userId1, Long userId2) {
        return friendshipRepository.areUsersFriends(userId1, userId2);
    }

    public FriendshipStatus getFriendshipStatus(Long userId1, Long userId2) {
        return friendshipRepository.findFriendshipBetweenUsers(userId1, userId2)
                .map(Friendship::getStatus)
                .orElse(null);
    }

    public List<User> getSuggestedFriends(Long userId) {
        return friendshipRepository.findSuggestedFriends(userId);
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
