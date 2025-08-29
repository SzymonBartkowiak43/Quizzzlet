package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.friendship.entity.FriendDto;
import com.example.quizlecikprojekt.domain.friendship.entity.Friendship;
import com.example.quizlecikprojekt.domain.friendship.entity.FriendshipDto;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.friendship.repository.FriendshipRepository;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    private final UserRepository userRepository;

    public FriendshipDto sendFriendRequest(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new InvalidOperationException("Nie możesz zaprosić siebie do przyjaźni");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));

        Optional<Friendship> existingFriendship = friendshipRepository
                .findFriendshipBetweenUsers(requesterId, addresseeId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new InvalidOperationException("Już jesteście przyjaciółmi");
            } else if (friendship.getStatus() == FriendshipStatus.PENDING) {
                throw new InvalidOperationException("Zaproszenie już zostało wysłane");
            } else if (friendship.getStatus() == FriendshipStatus.BLOCKED) {
                throw new InvalidOperationException("Nie możesz zaprosić tego użytkownika");
            }
        }

        Friendship newFriendship = new Friendship(requester, addressee);
        Friendship save = friendshipRepository.save(newFriendship);
        return fromEntity(save);
    }

    public Friendship acceptFriendRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Zaproszenie nie znalezione"));

        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new InvalidOperationException("Nie możesz zaakceptować tego zaproszenia");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("Zaproszenie nie jest aktywne");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    public void declineFriendRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Zaproszenie nie znalezione"));

        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new InvalidOperationException("Nie możesz odrzucić tego zaproszenia");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository
                .findFriendshipBetweenUsers(userId, friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Przyjaźń nie znaleziona"));

        if (!friendship.involves(userRepository.findById(userId).get())) {
            throw new InvalidOperationException("Nie możesz usunąć tej przyjaźni");
        }

        friendshipRepository.delete(friendship);
    }

    public void blockUser(Long userId, Long userToBlockId) {
        if (userId.equals(userToBlockId)) {
            throw new InvalidOperationException("Nie możesz zablokować siebie");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));
        User userToBlock = userRepository.findById(userToBlockId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));

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
