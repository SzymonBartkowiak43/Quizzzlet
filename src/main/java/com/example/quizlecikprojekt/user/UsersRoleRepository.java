package com.example.quizlecikprojekt.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRoleRepository extends CrudRepository<UsersRole, Long> {
    Optional<UsersRole> findByName(String name);
}
