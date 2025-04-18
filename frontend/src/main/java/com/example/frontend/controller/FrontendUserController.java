package com.example.frontend.controller;

import com.example.frontend.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendUserController {

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/signup")
    public String signupForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null)
            return "redirect:/index";
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/adduser")
    public String register(@ModelAttribute User user, Model model, HttpSession session) {
        System.out.println("FrontendUserController adduser");
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(
                    userServiceUrl + "/api/users/register", user, User.class);
            session.setAttribute("loggedInUser", response.getBody());
            return "redirect:/quiz-list";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Email already exists 1");
            return "add-user";
        } catch (Exception e) {
            model.addAttribute("error", "Service unavailable");
            return "add-user";
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null)
            return "redirect:/index";
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession session) {
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(
                    userServiceUrl + "/api/users/login", user, User.class);
            session.setAttribute("loggedInUser", response.getBody());
            return "redirect:/index";
        } catch (HttpClientErrorException.Unauthorized e) {
            model.addAttribute("error", true);
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

    @GetMapping({ "/", "/index" })
    public String showIndex(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", user);
        model.addAttribute("admin", user != null && "ADMIN".equals(user.getRole()));
        // Load quizzes from quiz-service here
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + loggedInUser.getId(), User.class);
            model.addAttribute("user", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load profile");
            return "redirect:/login";
        }

        return "profile";
    }

    @GetMapping("/profile2")
    public String profile2(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

}
