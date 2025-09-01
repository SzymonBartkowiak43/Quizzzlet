package com.example.quizlecikprojekt.domain.group;

import com.example.quizlecikprojekt.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMembers_Id(Long userId);
}
