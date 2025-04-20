package com.example.frontend.controller;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.model.User;
import com.example.frontend.dto.QuizDTO;
import com.example.frontend.model.Course;
import com.example.frontend.model.Enrolment;

@Controller
public class FrontendCourseController {

    @Value("${course.service.url}")
    private String courseServiceUrl;

    @Value("${enrolment.service.url}")
    private String enrolmentServiceUrl;
    
    @Value("${quiz.service.url}")
    private String quizServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping({"/", "/index"})
    public String showIndex(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);

        try {
            if ("STUDENT".equals(loggedInUser.getRole())) {
                ResponseEntity<Enrolment[]> enrolmentResponse = restTemplate.getForEntity(
                    enrolmentServiceUrl + "/enrolments/student/" + loggedInUser.getId(), Enrolment[].class);
                List<Enrolment> enrolments = Arrays.asList(enrolmentResponse.getBody());
    
                List<Course> courses = new ArrayList<>();
                for (Enrolment enrolment : enrolments) {
                    ResponseEntity<Course> courseResponse = restTemplate.getForEntity(
                        courseServiceUrl + "/courses/" + enrolment.getCourseId(), Course.class);
                    courses.add(courseResponse.getBody());
                }
    
                model.addAttribute("courses", courses);
            } else {
                ResponseEntity<Course[]> response = restTemplate.getForEntity(
                    courseServiceUrl + "/courses/teacher/" + loggedInUser.getId(), Course[].class);
                model.addAttribute("courses", Arrays.asList(response.getBody()));
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load courses.");
            model.addAttribute("courses", List.of());
        }
    
        return "index";
    }    

    @GetMapping("/courses")
    public String viewCourses(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);

        try {
            ResponseEntity<Course[]> response = restTemplate.getForEntity(
                courseServiceUrl + "/courses", Course[].class);
            List<Course> courses = Arrays.asList(response.getBody());
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch courses.");
        }

        return "all-courses";
    }

    @GetMapping("/courses/{id}")
    public String getCourseDetails(@PathVariable Long id, Model model, HttpSession session) {
        // Get loggedInUser through the HttpSession
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("STUDENT", loggedInUser != null && "STUDENT".equals(loggedInUser.getRole()));

        // Get course through course service
        try {
            ResponseEntity<Course> response = restTemplate.getForEntity(
                    courseServiceUrl + "/courses/" + id, Course.class);
            model.addAttribute("course", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load the course.");
        }

        // Check if student is already enrolled
        boolean alreadyEnrolled = false;
        if ("STUDENT".equalsIgnoreCase(loggedInUser.getRole())) {
            try {
                ResponseEntity<Enrolment[]> response = restTemplate.getForEntity(
                        enrolmentServiceUrl + "/enrolments/student/" + loggedInUser.getId(), Enrolment[].class);
                List<Enrolment> enrolments = Arrays.asList(response.getBody());

                alreadyEnrolled = enrolments.stream().anyMatch(e -> e.getCourseId().equals(Long.valueOf(id)));
            } catch (Exception e) {
                model.addAttribute("error", "Could not find enrollments.");
            }
        }
        model.addAttribute("alreadyEnrolled", alreadyEnrolled);

        // Get the quizzes that are part of a course through quiz service
        try {
            ResponseEntity<QuizDTO[]> response = restTemplate.getForEntity(
                quizServiceUrl + "/quizzes/byCourse/" + id, QuizDTO[].class);
            List<QuizDTO> quizzes = Arrays.asList(response.getBody());
            model.addAttribute("quizzes", quizzes);
        } catch (Exception e) {
            model.addAttribute("error", "Could not load quizzes.");
        }

        return "course-details";
    }

    @GetMapping("/create-course")
    public String showCreateCourseForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"TEACHER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/index";
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
            course.setTeacherId(user.getId());
            restTemplate.postForEntity(courseServiceUrl + "/courses", course, Course.class);
            return "redirect:/index";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create course.");
            return "create-course";
        }
    }

}