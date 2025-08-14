package com.example.quizlecikprojekt.domain.friendship.entity;

import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import com.example.quizlecikprojekt.domain.friendship.enums.MessageType;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_messages")
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_set_id")
    private WordSet sharedWordSet; // Opcjonalnie - udostępniony zestaw w grupie

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public GroupMessage() {}

    public GroupMessage(StudyGroup group, User sender, String content) {
        this.group = group;
        this.sender = sender;
        this.content = content;
        this.messageType = MessageType.TEXT;
    }

    public GroupMessage(StudyGroup group, User sender, String content, WordSet wordSet) {
        this.group = group;
        this.sender = sender;
        this.content = content;
        this.sharedWordSet = wordSet;
        this.messageType = MessageType.WORD_SET;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudyGroup getGroup() { return group; }
    public void setGroup(StudyGroup group) { this.group = group; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public WordSet getSharedWordSet() { return sharedWordSet; }
    public void setSharedWordSet(WordSet sharedWordSet) { this.sharedWordSet = sharedWordSet; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // Helper methods
    public boolean isFromUser(User user) {
        return sender.equals(user);
    }

    public boolean canUserSeeMessage(User user) {
        // Sprawdź czy user jest członkiem grupy
        return group.isUserMember(user);
    }

    public boolean canUserEditMessage(User user) {
        // Tylko sender może edytować swoją wiadomość
        return sender.equals(user);
    }

    public boolean canUserDeleteMessage(User user) {
        // Sender albo admin/moderator grupy może usunąć
        if (sender.equals(user)) return true;

        GroupMember membership = group.getUserMembership(user);
        return membership != null &&
                (membership.getRole() == GroupRole.ADMIN ||
                        membership.getRole() == GroupRole.MODERATOR);
    }
}