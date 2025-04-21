package com.example.frontend.dto;

import com.example.frontend.model.User;
import com.example.frontend.security.UserRole;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;

    public UserProfileDTO() {}

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}
