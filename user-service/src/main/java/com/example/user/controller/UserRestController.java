package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/users/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        System.out.println("============== Received Registration Request ==============");
        System.out.println("User object: " + user);
        System.out.println("Email: " + user.getEmail());
        System.out.println("Name:  " + user.getName());
        System.out.println("===========================================================");
        try {
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Email already exists 2");
        }
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existing = userService.findByEmail(user.getEmail());
        if (existing == null ||
            !existing.getPassword().equals(user.getPassword()) ||
            !existing.getName().equals(user.getName())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok(existing);
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
