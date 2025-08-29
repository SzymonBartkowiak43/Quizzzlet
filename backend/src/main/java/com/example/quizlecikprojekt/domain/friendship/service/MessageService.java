package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.friendship.entity.PrivateMessage;
import com.example.quizlecikprojekt.domain.friendship.repository.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final PrivateMessageRepository privateMessageRepository;

    private final UserRepository userRepository;

    private final WordSetRepository wordSetRepository;

    private final FriendshipRepository friendshipRepository;

    public PrivateMessage sendPrivateMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Nadawca nie znaleziony"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Odbiorca nie znaleziony"));

        if (!friendshipRepository.areUsersFriends(senderId, recipientId)) {
            throw new InvalidOperationException("Możesz wysyłać wiadomości tylko do przyjaciół");
        }

        PrivateMessage message = new PrivateMessage(sender, recipient, content);
        return privateMessageRepository.save(message);
    }

    public PrivateMessage sendWordSet(Long senderId, Long recipientId, String content, Long wordSetId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Nadawca nie znaleziony"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Odbiorca nie znaleziony"));
        WordSet wordSet = wordSetRepository.findById(wordSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Zestaw słówek nie znaleziony"));

        if (!friendshipRepository.areUsersFriends(senderId, recipientId)) {
            throw new InvalidOperationException("Możesz wysyłać wiadomości tylko do przyjaciół");
        }

        PrivateMessage message = new PrivateMessage(sender, recipient, content, wordSet);
        return privateMessageRepository.save(message);
    }

    public List<PrivateMessage> getConversation(Long userId1, Long userId2) {
        return privateMessageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    public List<PrivateMessage> getUserConversations(Long userId) {
        return privateMessageRepository.findUserConversations(userId);
    }

    public int markMessagesAsRead(Long recipientId, Long senderId) {
        return privateMessageRepository.markMessagesAsRead(recipientId, senderId);
    }

    public List<PrivateMessage> getUnreadMessages(Long userId) {
        return privateMessageRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }


    public Long getUnreadMessageCount(Long userId) {
        return privateMessageRepository.countUnreadMessages(userId);
    }


}
