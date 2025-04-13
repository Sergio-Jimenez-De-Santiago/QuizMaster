package com.example.frontend.controller;

import com.example.frontend.model.Quiz;
import com.example.frontend.model.User;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendQuizController {

        @Value("${quiz.service.url}")
        private String quizServiceUrl;

        private final RestTemplate restTemplate = new RestTemplate();

        @PostMapping("/createquiz")
        public String createQuiz(@ModelAttribute Quiz quiz, Model model, HttpSession session) {
                System.out.println("FrontendQuizController createQuiz");

                try {
                        ResponseEntity<Quiz> response = restTemplate.postForEntity(
                                        quizServiceUrl + "/api/quizzes", quiz, Quiz.class);

                        return "redirect:/quizzes"; // Or wherever you list/view quizzes
                } catch (HttpClientErrorException e) {
                        model.addAttribute("error", "Invalid quiz data: " + e.getMessage());
                        return "create-quiz";
                } catch (Exception e) {
                        model.addAttribute("error", "Quiz service unavailable");
                        return "create-quiz";
                }
        }

}
