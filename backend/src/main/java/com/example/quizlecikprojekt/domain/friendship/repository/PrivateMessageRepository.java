package com.example.quizlecikprojekt.domain.friendship.repository;

import com.example.quizlecikprojekt.entity.PrivateMessage;
import com.example.quizlecikprojekt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    // Konwersacja między dwoma użytkownikami
    @Query("SELECT pm FROM PrivateMessage pm WHERE " +
            "((pm.sender.id = :userId1 AND pm.recipient.id = :userId2) OR " +
            "(pm.sender.id = :userId2 AND pm.recipient.id = :userId1)) " +
            "ORDER BY pm.createdAt ASC")
    List<PrivateMessage> findConversationBetweenUsers(@Param("userId1") Long userId1,
                                                      @Param("userId2") Long userId2);

    // Konwersacja z paginacją
    @Query("SELECT pm FROM PrivateMessage pm WHERE " +
            "((pm.sender.id = :userId1 AND pm.recipient.id = :userId2) OR " +
            "(pm.sender.id = :userId2 AND pm.recipient.id = :userId1)) " +
            "ORDER BY pm.createdAt DESC")
    Page<PrivateMessage> findConversationBetweenUsersPageable(@Param("userId1") Long userId1,
                                                              @Param("userId2") Long userId2,
                                                              Pageable pageable);

    // Lista konwersacji użytkownika (ostatnie wiadomości z każdym)
    @Query("SELECT pm FROM PrivateMessage pm WHERE pm.id IN (" +
            "SELECT MAX(pm2.id) FROM PrivateMessage pm2 WHERE " +
            "(pm2.sender.id = :userId OR pm2.recipient.id = :userId) " +
            "GROUP BY CASE WHEN pm2.sender.id = :userId THEN pm2.recipient.id ELSE pm2.sender.id END" +
            ") ORDER BY pm.createdAt DESC")
    List<PrivateMessage> findUserConversations(@Param("userId") Long userId);

    // Nieprzeczytane wiadomości użytkownika
    List<PrivateMessage> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    // Liczba nieprzeczytanych wiadomości
    @Query("SELECT COUNT(pm) FROM PrivateMessage pm WHERE pm.recipient.id = :userId AND pm.isRead = false")
    Long countUnreadMessages(@Param("userId") Long userId);

    // Oznacz wiadomości jako przeczytane
    @Modifying
    @Query("UPDATE PrivateMessage pm SET pm.isRead = true WHERE " +
            "pm.recipient.id = :recipientId AND pm.sender.id = :senderId AND pm.isRead = false")
    int markMessagesAsRead(@Param("recipientId") Long recipientId, @Param("senderId") Long senderId);

    // Wiadomości od określonej daty
    @Query("SELECT pm FROM PrivateMessage pm WHERE " +
            "(pm.sender.id = :userId OR pm.recipient.id = :userId) AND pm.createdAt >= :fromDate " +
            "ORDER BY pm.createdAt DESC")
    List<PrivateMessage> findUserMessagesSince(@Param("userId") Long userId,
                                               @Param("fromDate") LocalDateTime fromDate);

    // Znajdź rozmówców użytkownika
    @Query("SELECT DISTINCT CASE WHEN pm.sender.id = :userId THEN pm.recipient ELSE pm.sender END " +
            "FROM PrivateMessage pm WHERE pm.sender.id = :userId OR pm.recipient.id = :userId")
    List<User> findUserChatPartners(@Param("userId") Long userId);
}
