package com.example.user.controller;

import com.example.user.assembler.UserDTOModelAssembler;
import com.example.user.model.User;
import com.example.user.dto.UserProfileDTO;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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

    @PostMapping(value = "/users", produces = "application/hal+json")
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

    @PostMapping(value = "/authenticate", produces = "application/hal+json")
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

    @GetMapping(value = "/users", produces = "application/hal+json")
    public ResponseEntity<CollectionModel<EntityModel<UserProfileDTO>>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<EntityModel<UserProfileDTO>> userModels = users.stream()
                .map(UserProfileDTO::new)
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(userModels,
                        linkTo(methodOn(UserRestController.class).getAllUsers()).withSelfRel()));
    }

    // Returns User object (password included)
    @GetMapping(value = "/users/{id}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(assembler.toModel(new UserProfileDTO(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Returns client safe user view (no password)
    @GetMapping(value = "/users/{id}/profile", produces = "application/hal+json")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }
}
