package com.example.quizlecikprojekt.domain.friendship.repository;

import com.example.quizlecikprojekt.entity.Friendship;
import com.example.quizlecikprojekt.domain.friendship.enums.FriendshipStatus;
import com.example.quizlecikprojekt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Znajdź przyjaźń między dwoma użytkownikami
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.id = :userId1 AND f.addressee.id = :userId2) OR " +
            "(f.requester.id = :userId2 AND f.addressee.id = :userId1)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("userId1") Long userId1,
                                                    @Param("userId2") Long userId2);

    // Znajdź wszystkie przyjaźnie użytkownika
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.id = :userId OR f.addressee.id = :userId) AND f.status = :status")
    List<Friendship> findUserFriendshipsByStatus(@Param("userId") Long userId,
                                                 @Param("status") FriendshipStatus status);

    // Otrzymane zaproszenia do przyjaźni
    @Query("SELECT f FROM Friendship f WHERE f.addressee.id = :userId AND f.status = 'PENDING'")
    List<Friendship> findPendingFriendRequestsForUser(@Param("userId") Long userId);

    // Wysłane zaproszenia do przyjaźni
    @Query("SELECT f FROM Friendship f WHERE f.requester.id = :userId AND f.status = 'PENDING'")
    List<Friendship> findSentFriendRequestsByUser(@Param("userId") Long userId);

    // Znajdź przyjaciół użytkownika
    @Query("SELECT u FROM User u WHERE u.id IN (" +
            "SELECT CASE WHEN f.requester.id = :userId THEN f.addressee.id ELSE f.requester.id END " +
            "FROM Friendship f WHERE (f.requester.id = :userId OR f.addressee.id = :userId) AND f.status = 'ACCEPTED')")
    List<User> findUserFriends(@Param("userId") Long userId);

    // Sprawdź czy użytkownicy są przyjaciółmi
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
            "((f.requester.id = :userId1 AND f.addressee.id = :userId2) OR " +
            "(f.requester.id = :userId2 AND f.addressee.id = :userId1)) AND f.status = 'ACCEPTED'")
    boolean areUsersFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Polecani znajomi (przyjaciele przyjaciół)
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN Friendship f1 ON (f1.requester = u OR f1.addressee = u) " +
            "JOIN Friendship f2 ON ((f1.requester.id = :userId AND f2.requester = f1.addressee) OR " +
            "                      (f1.addressee.id = :userId AND f2.requester = f1.requester) OR " +
            "                      (f1.requester.id = :userId AND f2.addressee = f1.addressee) OR " +
            "                      (f1.addressee.id = :userId AND f2.addressee = f1.requester)) " +
            "WHERE f1.status = 'ACCEPTED' AND f2.status = 'ACCEPTED' AND u.id != :userId " +
            "AND NOT EXISTS (SELECT 1 FROM Friendship f3 WHERE " +
            "((f3.requester.id = :userId AND f3.addressee.id = u.id) OR " +
            "(f3.requester.id = u.id AND f3.addressee.id = :userId)))")
    List<User> findSuggestedFriends(@Param("userId") Long userId);

    @Query("SELECT f.addressee FROM Friendship f WHERE f.requester.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findRequestedFriends(@Param("userId") Long userId);

    @Query("SELECT f.requester FROM Friendship f WHERE f.addressee.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findAddedFriends(@Param("userId") Long userId);
}