package com.example.quizlecikprojekt.domain.friendship.service;

import com.example.quizlecikprojekt.domain.friendship.entity.GroupMember;
import com.example.quizlecikprojekt.domain.friendship.entity.StudyGroup;
import com.example.quizlecikprojekt.domain.friendship.enums.GroupRole;
import com.example.quizlecikprojekt.domain.friendship.repository.GroupMemberRepository;
import com.example.quizlecikprojekt.domain.friendship.repository.StudyGroupRepository;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.exception.InvalidOperationException;
import com.example.quizlecikprojekt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    // Utwórz nową grupę
    public StudyGroup createGroup(Long creatorId, String name, String description, Boolean isPrivate) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));

        StudyGroup group = new StudyGroup(name, description, creator);
        if (isPrivate != null) {
            group.setIsPrivate(isPrivate);
        }

        return studyGroupRepository.save(group);
    }

    // Dołącz do grupy
    public GroupMember joinGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony"));
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));

        // Sprawdź czy już jest członkiem
        if (groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent()) {
            throw new InvalidOperationException("Już jesteś członkiem tej grupy");
        }

        // Sprawdź czy grupa nie jest pełna
        if (group.isFull()) {
            throw new InvalidOperationException("Grupa jest pełna");
        }

        GroupMember membership = new GroupMember(group, user, GroupRole.MEMBER);
        return groupMemberRepository.save(membership);
    }

    // Dołącz do grupy przez kod zaproszenia
    public GroupMember joinGroupByInviteCode(Long userId, String inviteCode) {
        StudyGroup group = studyGroupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException("Nieprawidłowy kod zaproszenia"));

        return joinGroup(userId, group.getId());
    }

    // Opuść grupę
    public void leaveGroup(Long userId, Long groupId) {
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie jesteś członkiem tej grupy"));

        StudyGroup group = membership.getGroup();

        // Sprawdź czy to nie ostatni admin
        if (membership.getRole() == GroupRole.ADMIN) {
            long adminCount = groupMemberRepository.findByGroupIdAndRole(groupId, GroupRole.ADMIN).size();
            if (adminCount <= 1) {
                throw new InvalidOperationException("Nie możesz opuścić grupy jako ostatni administrator");
            }
        }

        groupMemberRepository.delete(membership);

        // Usuń grupę jeśli nie ma członków
        if (groupMemberRepository.countGroupMembers(groupId) == 0) {
            studyGroupRepository.delete(group);
        }
    }

    // Usuń członka z grupy (tylko admin/moderator)
    public void removeMember(Long requesterId, Long groupId, Long memberToRemoveId) {
        // Sprawdź uprawnienia
        if (!groupMemberRepository.isUserAdminOrModerator(groupId, requesterId)) {
            throw new InvalidOperationException("Nie masz uprawnień do usuwania członków");
        }

        GroupMember membershipToRemove = groupMemberRepository
                .findByGroupIdAndUserId(groupId, memberToRemoveId)
                .orElseThrow(() -> new ResourceNotFoundException("Członek nie znaleziony"));

        // Nie można usunąć siebie
        if (requesterId.equals(memberToRemoveId)) {
            throw new InvalidOperationException("Nie możesz usunąć siebie - użyj opcji 'Opuść grupę'");
        }

        groupMemberRepository.delete(membershipToRemove);
    }

    // Zmień rolę członka (tylko admin)
    public GroupMember changeRole(Long adminId, Long groupId, Long memberId, GroupRole newRole) {
        // Sprawdź czy requester jest adminem
        GroupMember adminMembership = groupMemberRepository.findByGroupIdAndUserId(groupId, adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie jesteś członkiem tej grupy"));

        if (adminMembership.getRole() != GroupRole.ADMIN) {
            throw new InvalidOperationException("Tylko administratorzy mogą zmieniać role");
        }

        GroupMember memberToUpdate = groupMemberRepository.findByGroupIdAndUserId(groupId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Członek nie znaleziony"));

        memberToUpdate.setRole(newRole);
        return groupMemberRepository.save(memberToUpdate);
    }

    // Aktualizuj grupę
    public StudyGroup updateGroup(Long userId, Long groupId, String name, String description,
                                  Boolean isPrivate, Integer maxMembers) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));

        // Sprawdź uprawnienia
        if (!groupMemberRepository.isUserAdminOrModerator(groupId, userId)) {
            throw new InvalidOperationException("Nie masz uprawnień do edycji tej grupy");
        }

        if (name != null) group.setName(name);
        if (description != null) group.setDescription(description);
        if (isPrivate != null) group.setIsPrivate(isPrivate);
        if (maxMembers != null && maxMembers >= group.getMemberCount()) {
            group.setMaxMembers(maxMembers);
        }

        return studyGroupRepository.save(group);
    }

    // Usuń grupę
    public void deleteGroup(Long userId, Long groupId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));

        // Tylko twórca może usunąć grupę
        if (!group.getCreator().getId().equals(userId)) {
            throw new InvalidOperationException("Tylko twórca może usunąć grupę");
        }

        studyGroupRepository.delete(group);
    }

    // Pobierz grupy użytkownika
    public List<StudyGroup> getUserGroups(Long userId) {
        return studyGroupRepository.findGroupsByUserId(userId);
    }

    // Pobierz publiczne grupy
    public Page<StudyGroup> getPublicGroups(Pageable pageable) {
        return studyGroupRepository.findPublicGroups(pageable);
    }

    // Wyszukaj grupy
    public Page<StudyGroup> searchGroups(String searchTerm, Long userId, Pageable pageable) {
        return studyGroupRepository.searchGroups(searchTerm, userId, pageable);
    }

    // Pobierz członków grupy
    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId);
    }

    // Sprawdź czy użytkownik jest członkiem
    public boolean isUserMember(Long userId, Long groupId) {
        return studyGroupRepository.isUserMemberOfGroup(groupId, userId);
    }

    // Pobierz grupę po ID
    public StudyGroup getGroupById(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));
    }

    // Regeneruj kod zaproszenia
    public StudyGroup regenerateInviteCode(Long userId, Long groupId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa nie znaleziona"));

        if (!groupMemberRepository.isUserAdminOrModerator(groupId, userId)) {
            throw new InvalidOperationException("Nie masz uprawnień do zmiany kodu zaproszenia");
        }

        group.setInviteCode(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return studyGroupRepository.save(group);
    }
}
