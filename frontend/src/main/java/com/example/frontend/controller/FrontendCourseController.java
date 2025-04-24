package com.example.frontend.controller;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.model.User;
import com.example.frontend.response.QuizCollectionModel;
import com.example.frontend.security.UserRole;
import com.example.frontend.dto.QuizDTO;
import com.example.frontend.dto.UserProfileDTO;
import com.example.frontend.model.Course;
import com.example.frontend.model.Enrolment;
import com.example.frontend.model.Quiz;

@Controller
public class FrontendCourseController {

    @Value("${course.service.url}")
    private String courseServiceUrl;

    @Value("${enrolment.service.url}")
    private String enrolmentServiceUrl;

    @Value("${quiz.service.url}")
    private String quizServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping({ "/", "/index" })
    public String showIndex(Model model, HttpSession session) {
        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("isStudent", loggedInUser != null && loggedInUser.getRole() == UserRole.STUDENT);

        try {
            if (loggedInUser.getRole() == UserRole.STUDENT) {
                ResponseEntity<Enrolment[]> enrolmentResponse = restTemplate.getForEntity(
                        enrolmentServiceUrl + "/enrolments?studentId=" + loggedInUser.getId(), Enrolment[].class);
                List<Enrolment> enrolments = Arrays.asList(enrolmentResponse.getBody());

                System.out.println("Fetching enrolments for user: " + loggedInUser.getId());
                System.out.println("Enrolments: " + enrolments);

                List<Course> courses = new ArrayList<>();
                for (Enrolment enrolment : enrolments) {
                    Long courseId = enrolment.getCourseId();
                    System.out.println("Fetching course for courseId: " + courseId);
                    try {
                        ResponseEntity<Course> courseResponse = restTemplate.getForEntity(
                                courseServiceUrl + "/courses/" + courseId, Course.class);
                        Course course = courseResponse.getBody();
                        if (course != null) {
                            courses.add(course);
                        } else {
                            System.out.println("Warning: Received null for course ID " + courseId);
                        }
                    } catch (HttpClientErrorException | HttpServerErrorException e) {
                        System.out.println("Skipping course ID " + courseId + " due to error: " + e.getStatusCode()
                                + " - " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error fetching course ID " + courseId);
                        e.printStackTrace();
                    }
                }

                model.addAttribute("courses", courses);
            } else {
                ResponseEntity<Course[]> response = restTemplate.getForEntity(
                        courseServiceUrl + "/courses?teacherId=" + loggedInUser.getId(), Course[].class);
                model.addAttribute("courses", Arrays.asList(response.getBody()));
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load courses.");
            model.addAttribute("courses", List.of());
            e.printStackTrace();
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
        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedInUser", loggedInUser);

        // Get course through course service
        Course course = null;
        try {
            ResponseEntity<Course> response = restTemplate.getForEntity(
                    courseServiceUrl + "/courses/" + id, Course.class);
            course = response.getBody();
            model.addAttribute("course", course);
        } catch (Exception e) {
            model.addAttribute("error", "Could not load the course.");
            return "course-details";
        }

        // Determine user roles and access
        boolean isOwner = loggedInUser.getRole() == UserRole.TEACHER
                && course.getTeacherId().equals(loggedInUser.getId());
        boolean isStudent = loggedInUser.getRole() == UserRole.STUDENT;
        boolean isStudentView = isStudent || (!isOwner && loggedInUser.getRole() == UserRole.TEACHER);

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isStudentView", isStudentView);

        // Check if student is already enrolled
        boolean alreadyEnrolled = false;
        if (isStudent) {
            try {
                ResponseEntity<Enrolment[]> response = restTemplate.getForEntity(
                        enrolmentServiceUrl + "/enrolments?studentId=" + loggedInUser.getId() + "&courseId=" + id,
                        Enrolment[].class);
                alreadyEnrolled = response.getBody() != null && response.getBody().length > 0;
            } catch (Exception e) {
                model.addAttribute("error", "Could not find enrollments.");
            }
        }
        model.addAttribute("alreadyEnrolled", alreadyEnrolled);

        // Get quizzes that are part of the course
        try {
            ResponseEntity<QuizCollectionModel> response = restTemplate.getForEntity(
                    quizServiceUrl + "/quizzes?courseId=" + id, QuizCollectionModel.class);

            List<EntityModel<QuizDTO>> quizModels = response.getBody().getQuizzes();

            List<QuizDTO> quizzes = quizModels.stream()
                    .map(EntityModel::getContent)
                    .collect(Collectors.toList());

            model.addAttribute("quizzes", quizzes);
        } catch (Exception e) {
            model.addAttribute("error", "Could not load quizzes.");
        }

        return "course-details";
    }

    @GetMapping("/create-course")
    public String showCreateCourseForm(Model model, HttpSession session) {
        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");

        if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
            return "redirect:/index";
        }

        model.addAttribute("course", new Course());
        model.addAttribute("loggedInUser", loggedInUser);

        return "create-course";
    }

    @PostMapping("/courses/create")
    public String createCourse(@ModelAttribute Course course, Model model, HttpSession session) {
        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");

        if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
            return "redirect:/login";
        }

        try {
            course.setTeacherId(loggedInUser.getId());
            restTemplate.postForEntity(courseServiceUrl + "/courses", course, Course.class);
            return "redirect:/index";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create course.");
            return "create-course";
        }
    }

}