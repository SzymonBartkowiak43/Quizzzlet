package com.example.quizlecikprojekt.user;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @ManyToMany
    @JoinTable (
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<UsersRole> roles = new HashSet<>();
}
