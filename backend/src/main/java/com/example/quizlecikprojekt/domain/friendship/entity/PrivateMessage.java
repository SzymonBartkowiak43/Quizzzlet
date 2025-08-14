package com.example.quizlecikprojekt.domain.friendship.entity;

import com.example.quizlecikprojekt.domain.friendship.enums.MessageType;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "private_messages")
public class PrivateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_set_id")
    private WordSet sharedWordSet; // Opcjonalnie - udostępniony zestaw

    @Column(name = "is_read")
    private Boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public PrivateMessage() {}

    public PrivateMessage(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.messageType = MessageType.TEXT;
    }

    public PrivateMessage(User sender, User recipient, String content, WordSet wordSet) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.sharedWordSet = wordSet;
        this.messageType = MessageType.WORD_SET;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public WordSet getSharedWordSet() { return sharedWordSet; }
    public void setSharedWordSet(WordSet sharedWordSet) { this.sharedWordSet = sharedWordSet; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isFromUser(User user) {
        return sender.equals(user);
    }

    public boolean isToUser(User user) {
        return recipient.equals(user);
    }
}