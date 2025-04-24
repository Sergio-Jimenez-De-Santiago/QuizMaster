package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.security.UserRole;
import com.example.user.dto.UserProfileDTO;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    @Autowired
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users", produces = "application/hal+json")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        try {
            User created = userService.createUser(user);
            EntityModel<UserProfileDTO> resource = toModel(new UserProfileDTO(created), ViewContext.REGISTER);
            return ResponseEntity
                    .created(linkTo(methodOn(UserRestController.class).getUserProfile(created.getId())).toUri())
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Email already exists");
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

        EntityModel<UserProfileDTO> resource = toModel(new UserProfileDTO(existing), ViewContext.LOGIN);
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/users/{id}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(toModel(new UserProfileDTO(user), ViewContext.API_VIEW));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/users/{id}/profile", produces = "application/hal+json")
    public ResponseEntity<EntityModel<UserProfileDTO>> getUserProfile(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(toModel(new UserProfileDTO(user), ViewContext.PROFILE_VIEW));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private EntityModel<UserProfileDTO> toModel(UserProfileDTO user, ViewContext context) {
        EntityModel<UserProfileDTO> model;

        switch (context) {
            case PROFILE_VIEW -> {
                model = EntityModel.of(user,
                        linkTo(methodOn(UserRestController.class).getUserProfile(user.getId())).withSelfRel());
            }
            case LOGIN, REGISTER -> {
                model = EntityModel.of(user,
                        linkTo(methodOn(UserRestController.class).getUser(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserRestController.class).getUserProfile(user.getId())).withRel("profile"));
            }
            case API_VIEW -> {
                model = EntityModel.of(user,
                        linkTo(methodOn(UserRestController.class).getUser(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserRestController.class).getUserProfile(user.getId())).withRel("profile"));
            }
            default -> {
                model = EntityModel.of(user,
                        linkTo(methodOn(UserRestController.class).getUser(user.getId())).withSelfRel());
            }
        }

        return model;
    }

    private enum ViewContext {
        LOGIN,
        REGISTER,
        PROFILE_VIEW,
        API_VIEW
    }
}
