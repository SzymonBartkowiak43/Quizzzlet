package pl.bartkowiak.quizlecikprojekt.domain.user;

import java.util.Optional;

import pl.bartkowiak.quizlecikprojekt.entity.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
  Optional<UserRole> findByName(String name);
}
