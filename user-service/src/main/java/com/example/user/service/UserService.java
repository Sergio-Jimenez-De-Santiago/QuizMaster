package com.example.user.service;

import com.example.user.assembler.UserDTOModelAssembler;
import com.example.user.dto.UserProfileDTO;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private UserDTOModelAssembler assembler;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(@Valid User user) {
        User existing = userRepository.findByEmail(user.getEmail());

        if (existing != null) {
            throw new IllegalArgumentException("Email already exists 3");
        }
    
        return userRepository.save(user);
    }
    

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
    }    

    public ResponseEntity<EntityModel<UserProfileDTO>> getUserProfile(Long id) {
        User user = findById(id);
        return ResponseEntity.ok(assembler.toModel(new UserProfileDTO(user)));
    }
}
