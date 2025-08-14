package com.example.quizlecikprojekt.domain.friendship.repository;

import com.example.quizlecikprojekt.domain.friendship.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    // Wiadomości grupy
    List<GroupMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // Wiadomości grupy z paginacją
    Page<GroupMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    // Najnowsze wiadomości grupy
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId " +
            "ORDER BY gm.createdAt DESC")
    List<GroupMessage> findRecentGroupMessages(@Param("groupId") Long groupId, Pageable pageable);

    // Wiadomości od określonej daty
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId AND gm.createdAt >= :fromDate " +
            "ORDER BY gm.createdAt ASC")
    List<GroupMessage> findGroupMessagesSince(@Param("groupId") Long groupId,
                                              @Param("fromDate") LocalDateTime fromDate);

    // Liczba wiadomości w grupie
    @Query("SELECT COUNT(gm) FROM GroupMessage gm WHERE gm.group.id = :groupId")
    Long countGroupMessages(@Param("groupId") Long groupId);

    // Wiadomości użytkownika w grupie
    List<GroupMessage> findByGroupIdAndSenderIdOrderByCreatedAtDesc(Long groupId, Long senderId);

    // Ostatnia wiadomość w grupie
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId " +
            "ORDER BY gm.createdAt DESC LIMIT 1")
    GroupMessage findLastGroupMessage(@Param("groupId") Long groupId);

    // Grupy z najnowszymi wiadomościami dla użytkownika
    @Query("SELECT gm FROM GroupMessage gm " +
            "JOIN GroupMember mem ON mem.group.id = gm.group.id " +
            "WHERE mem.user.id = :userId AND gm.id IN (" +
            "SELECT MAX(gm2.id) FROM GroupMessage gm2 GROUP BY gm2.group.id" +
            ") ORDER BY gm.createdAt DESC")
    List<GroupMessage> findLatestMessagesForUserGroups(@Param("userId") Long userId);
}