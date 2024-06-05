package com.example.quizlecikprojekt.user;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/{id}")
    ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return usersService.getUsersById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    ResponseEntity<Users> saveUser(@RequestBody Users user) {
        Users saveUser = usersService.saveUsers(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saveUser.getId())
                .toUri();
        return ResponseEntity.created(uri).body(saveUser);
    }

    @PutMapping()
    ResponseEntity<Users> updateUser(@RequestBody Users user) {
        Optional<Users> updatedUser = usersService.getUsersById(user.getId());
        if(!user.getUserName().isEmpty()) {
            updatedUser.get().setUserName(user.getUserName());
        }
        if(!user.getEmail().isEmpty()) {
            updatedUser.get().setEmail(user.getEmail());
        }
        if(!user.getPassword().isEmpty()) {
            updatedUser.get().setPassword(user.getPassword());
        }
        return usersService.updateUsers(user) //przciez kurwa nie wiesz jakie id ma ten ziomek a moze
        // wiesz bo to jego profil, kutwa muisz pomyslec


    }
}
