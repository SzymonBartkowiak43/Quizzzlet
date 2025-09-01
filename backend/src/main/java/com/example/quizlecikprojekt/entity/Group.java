package com.example.quizlecikprojekt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_group")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "chat_group_members", // też zmieniamy nazwę!
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    public Group() {}

    public Group(String name, User creator, Set<User> members) {
        this.name = name;
        this.creator = creator;
        this.members = members;
    }

}