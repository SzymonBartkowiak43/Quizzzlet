package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.friendship.entity.*;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  @Email(message = "must be a well-formed email address")
  private String email;

  @Column(nullable = false, unique = true)
  private String name;

  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Set<UserRole> roles = new HashSet<>();

  User addUserRole(UserRole userRole) {
    if (roles == null) roles = new HashSet<>();
    roles.add(userRole);
    return this;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }



  @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL)
  private Set<Friendship> sentFriendRequests = new HashSet<>();

  @OneToMany(mappedBy = "addressee", cascade = CascadeType.ALL)
  private Set<Friendship> receivedFriendRequests = new HashSet<>();

  @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
  private Set<StudyGroup> createdGroups = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<GroupMember> groupMemberships = new HashSet<>();

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<PrivateMessage> sentMessages = new HashSet<>();

  @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
  private Set<PrivateMessage> receivedMessages = new HashSet<>();

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<GroupMessage> groupMessages = new HashSet<>();

  // Helper methods dla przyjaźni
  public Set<User> getFriends() {
    Set<User> friends = new HashSet<>();

    // Przyjaźnie gdzie jestem requester
    sentFriendRequests.stream()
            .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
            .forEach(f -> friends.add(f.getAddressee()));

    // Przyjaźnie gdzie jestem addressee
    receivedFriendRequests.stream()
            .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
            .forEach(f -> friends.add(f.getRequester()));

    return friends;
  }

  public Set<StudyGroup> getJoinedGroups() {
    return groupMemberships.stream()
            .map(GroupMember::getGroup)
            .collect(Collectors.toSet());
  }

  // Getters and Setters dla nowych pól
  public Set<Friendship> getSentFriendRequests() { return sentFriendRequests; }
  public void setSentFriendRequests(Set<Friendship> sentFriendRequests) { this.sentFriendRequests = sentFriendRequests; }

  public Set<Friendship> getReceivedFriendRequests() { return receivedFriendRequests; }
  public void setReceivedFriendRequests(Set<Friendship> receivedFriendRequests) { this.receivedFriendRequests = receivedFriendRequests; }

  public Set<StudyGroup> getCreatedGroups() { return createdGroups; }
  public void setCreatedGroups(Set<StudyGroup> createdGroups) { this.createdGroups = createdGroups; }

  public Set<GroupMember> getGroupMemberships() { return groupMemberships; }
  public void setGroupMemberships(Set<GroupMember> groupMemberships) { this.groupMemberships = groupMemberships; }

  public Set<PrivateMessage> getSentMessages() { return sentMessages; }
  public void setSentMessages(Set<PrivateMessage> sentMessages) { this.sentMessages = sentMessages; }

  public Set<PrivateMessage> getReceivedMessages() { return receivedMessages; }
  public void setReceivedMessages(Set<PrivateMessage> receivedMessages) { this.receivedMessages = receivedMessages; }

  public Set<GroupMessage> getGroupMessages() { return groupMessages; }
  public void setGroupMessages(Set<GroupMessage> groupMessages) { this.groupMessages = groupMessages; }
}
