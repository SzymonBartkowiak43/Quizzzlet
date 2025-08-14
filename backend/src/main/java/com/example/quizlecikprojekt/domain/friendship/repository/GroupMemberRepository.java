package com.example.quizlecikprojekt.domain.friendship.repository;

import com.example.quizlecikprojekt.domain.friendship.entity.GroupMember;
import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // Znajdź członkostwo użytkownika w grupie
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    // Członkowie grupy
    List<GroupMember> findByGroupIdOrderByJoinedAtAsc(Long groupId);

    // Członkowie grupy z określoną rolą
    List<GroupMember> findByGroupIdAndRole(Long groupId, GroupRole role);

    // Administracja grup użytkownika
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user.id = :userId AND gm.role IN ('ADMIN', 'MODERATOR')")
    List<GroupMember> findUserAdminRoles(@Param("userId") Long userId);

    // Liczba członków grupy
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId")
    Long countGroupMembers(@Param("groupId") Long groupId);

    // Sprawdź czy użytkownik ma uprawnienia administratora w grupie
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE " +
            "gm.group.id = :groupId AND gm.user.id = :userId AND gm.role IN ('ADMIN', 'MODERATOR')")
    boolean isUserAdminOrModerator(@Param("groupId") Long groupId, @Param("userId") Long userId);
}