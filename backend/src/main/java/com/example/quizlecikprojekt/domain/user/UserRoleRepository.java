package com.example.quizlecikprojekt.domain.user;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

interface UserRoleRepository extends CrudRepository<UserRole, Long> {
  Optional<UserRole> findByName(String name);
}
