package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.friendship.entity.GroupMessage;
import com.example.quizlecikprojekt.domain.friendship.entity.PrivateMessage;
import com.example.quizlecikprojekt.domain.friendship.entity.StudyGroup;
import com.example.quizlecikprojekt.domain.friendship.repository.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private WordSetRepository wordSetRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    // === PRYWATNE WIADOMOŚCI ===

    // Wyślij prywatną wiadomość tekstową
    public PrivateMessage sendPrivateMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Nadawca nie znaleziony"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Odbiorca nie znaleziony"));

        // Sprawdź czy użytkownicy są przyjaciółmi (opcjonalne)
        if (!friendshipRepository.areUsersFriends(senderId, recipientId)) {
            throw new InvalidOperationException("Możesz wysyłać wiadomości tylko do przyjaciół");
        }

        PrivateMessage message = new PrivateMessage(sender, recipient, content);
        return privateMessageRepository.save(message);
    }

    // Wyślij zestaw słówek
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

    // Pobierz konwersację między użytkownikami
    public List<PrivateMessage> getConversation(Long userId1, Long userId2) {
        return privateMessageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    // Pobierz konwersację z paginacją
    public Page<PrivateMessage> getConversationPageable(Long userId1, Long userId2, Pageable pageable) {
        return privateMessageRepository.findConversationBetweenUsersPageable(userId1, userId2, pageable);
    }

    // Pobierz listę konwersacji użytkownika
    public List<PrivateMessage> getUserConversations(Long userId) {
        return privateMessageRepository.findUserConversations(userId);
    }

    // Oznacz wiadomości jako przeczytane
    public int markMessagesAsRead(Long recipientId, Long senderId) {
        return privateMessageRepository.markMessagesAsRead(recipientId, senderId);
    }

    // Pobierz nieprzeczytane wiadomości
    public List<PrivateMessage> getUnreadMessages(Long userId) {
        return privateMessageRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // Liczba nieprzeczytanych wiadomości
    public Long getUnreadMessageCount(Long userId) {
        return privateMessageRepository.countUnreadMessages(userId);
    }

    // === WIADOMOŚCI GRUPOWE ===

    // Wyślij wiadomość do grupy
    public GroupMessage sendGroupMessage(Long senderId, Long groupId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Nadawca nie znaleziony"));
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));

        // Sprawdź czy użytkownik jest członkiem grupy
        if (!groupMemberRepository.findByGroupIdAndUserId(groupId, senderId).isPresent()) {
            throw new InvalidOperationException("Nie jesteś członkiem tej grupy");
        }

        GroupMessage message = new GroupMessage(group, sender, content);
        return groupMessageRepository.save(message);
    }

    // Wyślij zestaw słówek do grupy
    public GroupMessage sendWordSetToGroup(Long senderId, Long groupId, String content, Long wordSetId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Nadawca nie znaleziony"));
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));
        WordSet wordSet = wordSetRepository.findById(wordSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Zestaw słówek nie znaleziony"));

        if (!groupMemberRepository.findByGroupIdAndUserId(groupId, senderId).isPresent()) {
            throw new InvalidOperationException("Nie jesteś członkiem tej grupy");
        }

        GroupMessage message = new GroupMessage(group, sender, content, wordSet);
        return groupMessageRepository.save(message);
    }

    // Pobierz wiadomości grupy
    public List<GroupMessage> getGroupMessages(Long groupId) {
        return groupMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    // Pobierz wiadomości grupy z paginacją
    public Page<GroupMessage> getGroupMessagesPageable(Long groupId, Pageable pageable) {
        return groupMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
    }

    // Usuń wiadomość prywatną
    public void deletePrivateMessage(Long userId, Long messageId) {
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiadomość nie znaleziona"));

        if (!message.getSender().getId().equals(userId)) {
            throw new InvalidOperationException("Możesz usuwać tylko swoje wiadomości");
        }

        privateMessageRepository.delete(message);
    }

    // Usuń wiadomość grupową
    public void deleteGroupMessage(Long userId, Long messageId) {
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiadomość nie znaleziona"));

        if (!message.canUserDeleteMessage(userRepository.findById(userId).get())) {
            throw new InvalidOperationException("Nie masz uprawnień do usunięcia tej wiadomości");
        }

        groupMessageRepository.delete(message);
    }
}
