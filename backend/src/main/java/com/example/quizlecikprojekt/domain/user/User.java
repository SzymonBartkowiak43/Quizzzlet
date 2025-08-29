package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.friendship.entity.*;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.domain.group.GroupMessage;
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

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<PrivateMessage> sentMessages = new HashSet<>();

  @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
  private Set<PrivateMessage> receivedMessages = new HashSet<>();

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<GroupMessage> groupMessages = new HashSet<>();


}
