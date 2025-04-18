package com.example.frontend.controller;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.model.User;
import com.example.frontend.model.Course;
import com.example.frontend.model.Quiz;

@Controller
public class FrontendCourseController {

    @Value("${course.service.url}")
    private String moduleServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/modules")
    public String viewModules(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            ResponseEntity<Course[]> response = restTemplate.getForEntity(
                    moduleServiceUrl + "/api/modules", Course[].class);
            List<Course> modules = Arrays.asList(response.getBody());
            model.addAttribute("modules", modules);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch modules.");
        }

        return "all-modules";
    }

    @GetMapping("/modules/{id}")
    public String getQuiz(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            ResponseEntity<Course> response = restTemplate.getForEntity(
                    moduleServiceUrl + "/modules/" + id, Course.class);
            model.addAttribute("module", response.getBody());
            System.out.println("Got module with id: " + id);
            System.out.println(response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load the module.");
        }
        Object userObj = session.getAttribute("loggedInUser");
        User user = (User) userObj;
        model.addAttribute("USER", user != null && "USER".equals(user.getRole()));
        System.out.println(user.getRole());
        model.addAttribute("loggedInUser", user);
        return "module-details";
    }

    @GetMapping("/create-module")
    public String showCreateModuleForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("module", new Course());
        model.addAttribute("loggedInUser", user);

        return "create-module";
    }

    @PostMapping("/modules/create")
    public String createModule(@ModelAttribute Course module, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"TEACHER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        try {
            module.setTeacherId(user.getId()); // assuming module has a teacherId field
            restTemplate.postForEntity(moduleServiceUrl + "/api/modules", module, Course.class);
            return "redirect:/modules";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create module.");
            return "create-module";
        }
    }

}