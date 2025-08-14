package com.example.quizlecikprojekt.domain.friendship.entity;

import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import com.example.quizlecikprojekt.domain.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Column(name = "max_members")
    private Integer maxMembers = 50;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMember> members = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMessage> messages = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public StudyGroup() {}

    public StudyGroup(String name, String description, User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.inviteCode = generateInviteCode();

        // Dodaj twórcę jako admin
        GroupMember creatorMember = new GroupMember(this, creator, GroupRole.ADMIN);
        this.members.add(creatorMember);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public Set<GroupMember> getMembers() { return members; }
    public void setMembers(Set<GroupMember> members) { this.members = members; }

    public Set<GroupMessage> getMessages() { return messages; }
    public void setMessages(Set<GroupMessage> messages) { this.messages = messages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Helper methods
    public int getMemberCount() {
        return members.size();
    }

    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    public boolean isUserMember(User user) {
        return members.stream()
                .anyMatch(member -> member.getUser().equals(user));
    }

    public GroupMember getUserMembership(User user) {
        return members.stream()
                .filter(member -> member.getUser().equals(user))
                .findFirst()
                .orElse(null);
    }
}