package pl.bartkowiak.quizlecikprojekt.domain.group;

import pl.bartkowiak.quizlecikprojekt.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMembers_Id(Long userId);
}
