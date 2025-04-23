package com.example.user.controller;

import com.example.user.assembler.UserDTOModelAssembler;
import com.example.user.model.User;
import com.example.user.dto.UserProfileDTO;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    @Autowired
    private final UserService userService;

    @Autowired
    private UserDTOModelAssembler assembler;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        System.out.println("============== Received Registration Request ==============");
        System.out.println("User object: " + user);
        System.out.println("Email: " + user.getEmail());
        System.out.println("Name:  " + user.getName());
        System.out.println("===========================================================");
        try {
            User created = userService.createUser(user);
            EntityModel<UserProfileDTO> resource = assembler.toModel(new UserProfileDTO(created));
            return ResponseEntity
                    .created(linkTo(methodOn(UserRestController.class).getUserProfile(created.getId())).toUri())
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Email already exists 2");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existing = userService.findByEmail(user.getEmail());

        if (existing == null ||
                !existing.getPassword().equals(user.getPassword()) ||
                !existing.getName().equals(user.getName())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        EntityModel<UserProfileDTO> resource = assembler.toModel(new UserProfileDTO(existing));
        return ResponseEntity.ok(resource);
    }

    // Returns User object (password included)
    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(assembler.toModel(new UserProfileDTO(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Returns client safe user view (no password)
    @GetMapping("/users/{id}/profile")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }
}
