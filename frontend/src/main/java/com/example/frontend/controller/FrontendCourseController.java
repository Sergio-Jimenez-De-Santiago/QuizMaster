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

@Controller
public class FrontendCourseController {

    @Value("${course.service.url}")
    private String courseServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/courses")
    public String viewCourses(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            ResponseEntity<Course[]> response = restTemplate.getForEntity(
                courseServiceUrl + "/api/courses", Course[].class);
            List<Course> courses = Arrays.asList(response.getBody());
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch courses.");
        }

        return "all-courses";
    }

    @GetMapping("/courses/{id}")
    public String getQuiz(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            ResponseEntity<Course> response = restTemplate.getForEntity(
                courseServiceUrl + "/courses/" + id, Course.class);
            model.addAttribute("course", response.getBody());
            System.out.println("Got course with id: " + id);
            System.out.println(response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load the course.");
        }
        Object userObj = session.getAttribute("loggedInUser");
        User user = (User) userObj;
        model.addAttribute("USER", user != null && "USER".equals(user.getRole()));
        System.out.println(user.getRole());
        model.addAttribute("loggedInUser", user);
        return "course-details";
    }

    @GetMapping("/create-course")
    public String showCreateCourseForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("course", new Course());
        model.addAttribute("loggedInUser", user);

        return "create-course";
    }

    @PostMapping("/courses/create")
    public String createCourse(@ModelAttribute Course course, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"TEACHER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        try {
            course.setTeacherId(user.getId()); // assuming course has a teacherId field
            restTemplate.postForEntity(courseServiceUrl + "/api/courses", course, Course.class);
            return "redirect:/courses";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create course.");
            return "create-course";
        }
    }

}