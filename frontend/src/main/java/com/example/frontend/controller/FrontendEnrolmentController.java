package com.example.frontend.controller;

import com.example.frontend.dto.UserProfileDTO;
import com.example.frontend.model.Course;
import com.example.frontend.model.Enrolment;
import com.example.frontend.model.User;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendEnrolmentController {

    @Value("${enrolment.service.url}")
    private String enrolementServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId, HttpSession session) {
        try {
            UserProfileDTO student = (UserProfileDTO) session.getAttribute("loggedInUser");
            if (student == null) {
                return "redirect:/login";
            }

            Enrolment enrolment = new Enrolment();
            enrolment.setCourseId(courseId);
            enrolment.setStudentId(student.getId());
            restTemplate.postForEntity(enrolementServiceUrl + "/enrolments", enrolment, Enrolment.class);

            return "redirect:/courses";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/courses/" + courseId + "?error=" + e.getMessage();
        }
    }

}
