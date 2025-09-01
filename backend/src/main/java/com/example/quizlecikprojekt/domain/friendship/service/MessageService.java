package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.entity.PrivateMessage;
import com.example.quizlecikprojekt.domain.friendship.repository.*;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final PrivateMessageRepository privateMessageRepository;

    private final UserFacade userFacade;

    private final FriendshipRepository friendshipRepository;

    public PrivateMessage sendPrivateMessage(Long senderId, Long recipientId, String content) {
        User sender = userFacade.getUserById(senderId);
        User recipient = userFacade.getUserById(recipientId);

        if (!friendshipRepository.areUsersFriends(senderId, recipientId)) {
            throw new InvalidOperationException("Możesz wysyłać wiadomości tylko do przyjaciół");
        }

        PrivateMessage message = new PrivateMessage(sender, recipient, content);
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
