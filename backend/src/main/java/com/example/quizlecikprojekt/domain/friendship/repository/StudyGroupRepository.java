package com.example.quizlecikprojekt.domain.friendship.repository;


import com.example.quizlecikprojekt.domain.friendship.entity.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    // Znajdź grupę po kodzie zaproszenia
    Optional<StudyGroup> findByInviteCode(String inviteCode);

    // Grupy użytkownika (jako członek)
    @Query("SELECT sg FROM StudyGroup sg JOIN sg.members gm WHERE gm.user.id = :userId")
    List<StudyGroup> findGroupsByUserId(@Param("userId") Long userId);

    // Grupy utworzone przez użytkownika
    List<StudyGroup> findByCreatorId(Long creatorId);

    // Publiczne grupy do przeglądania
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.isPrivate = false ORDER BY sg.createdAt DESC")
    Page<StudyGroup> findPublicGroups(Pageable pageable);

    // Wyszukiwanie grup po nazwie
    @Query("SELECT sg FROM StudyGroup sg WHERE " +
            "LOWER(sg.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
            "(sg.isPrivate = false OR sg.creator.id = :userId OR " +
            "EXISTS (SELECT 1 FROM GroupMember gm WHERE gm.group.id = sg.id AND gm.user.id = :userId))")
    Page<StudyGroup> searchGroups(@Param("searchTerm") String searchTerm,
                                  @Param("userId") Long userId,
                                  Pageable pageable);

    // Popularne grupy (najwięcej członków)
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.isPrivate = false " +
            "ORDER BY SIZE(sg.members) DESC")
    List<StudyGroup> findPopularGroups(Pageable pageable);

    // Sprawdź czy użytkownik jest członkiem grupy
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    boolean isUserMemberOfGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // Grupy z najnowszymi wiadomościami
    @Query("SELECT DISTINCT sg FROM StudyGroup sg " +
            "LEFT JOIN sg.messages gm " +
            "JOIN sg.members mem WHERE mem.user.id = :userId " +
            "ORDER BY gm.createdAt DESC")
    List<StudyGroup> findUserGroupsWithRecentActivity(@Param("userId") Long userId);
}