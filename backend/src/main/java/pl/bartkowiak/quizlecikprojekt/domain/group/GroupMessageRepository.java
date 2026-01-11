package pl.bartkowiak.quizlecikprojekt.domain.group;

import pl.bartkowiak.quizlecikprojekt.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroup_IdOrderByCreatedAtAsc(Long groupId);
}