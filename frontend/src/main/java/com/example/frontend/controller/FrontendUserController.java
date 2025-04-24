package com.example.frontend.controller;

import com.example.frontend.dto.UserProfileDTO;
import com.example.frontend.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
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
            ParameterizedTypeReference<EntityModel<UserProfileDTO>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<EntityModel<UserProfileDTO>> response = restTemplate.exchange(
                    userServiceUrl + "/users",
                    HttpMethod.POST,
                    new HttpEntity<>(user),
                    responseType);
            session.setAttribute("loggedInUser", response.getBody().getContent());
            return "redirect:/index";
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
            ParameterizedTypeReference<EntityModel<UserProfileDTO>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<EntityModel<UserProfileDTO>> response = restTemplate.exchange(
                    userServiceUrl + "/authenticate",
                    HttpMethod.POST,
                    new HttpEntity<>(user),
                    responseType);
            session.setAttribute("loggedInUser", response.getBody().getContent());
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

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);

        try {
            ParameterizedTypeReference<EntityModel<UserProfileDTO>> responseType = new ParameterizedTypeReference<EntityModel<UserProfileDTO>>() {};
            ResponseEntity<EntityModel<UserProfileDTO>> response = restTemplate.exchange(
                    userServiceUrl + "/users/" + loggedInUser.getId() + "/profile",
                    HttpMethod.GET,
                    null,
                    responseType);

            UserProfileDTO profile = response.getBody().getContent();
            model.addAttribute("user", profile);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }

        return "profile";
    }
}
