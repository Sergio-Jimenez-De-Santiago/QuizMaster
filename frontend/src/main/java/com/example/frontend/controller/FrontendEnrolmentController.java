package com.example.frontend.controller;

import com.example.frontend.model.Enrolment;
import com.example.frontend.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendEnrolmentController {

    @Value("${enrolment.service.url}")
    private String enrolementServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId, HttpSession session) {
        try {
            User student = (User) session.getAttribute("loggedInUser");

            if (student == null) {
                return "redirect:/login";
            }

            Enrolment enrolment = new Enrolment();
            enrolment.setCourseId(courseId);
            enrolment.setStudentId(student.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Enrolment> request = new HttpEntity<>(enrolment, headers);

            ResponseEntity<Enrolment> response = restTemplate.postForEntity(
                    enrolementServiceUrl + "/enrolments", request, Enrolment.class);

            System.out.println("success" + response);
            return "redirect:/courses";

        } catch (Exception e) {
            e.printStackTrace();

            return "redirect:/courses/" + courseId + "?error=" + e.getMessage();
        }
    }

}
