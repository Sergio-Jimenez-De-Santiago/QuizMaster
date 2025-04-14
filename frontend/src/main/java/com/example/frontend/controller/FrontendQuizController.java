package com.example.frontend.controller;

import com.example.frontend.dto.QuizDTO;
import com.example.frontend.model.Quiz;
import com.example.frontend.model.User;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class FrontendQuizController {

        @Value("${quiz.service.url}")
        private String quizServiceUrl; // URL for the quiz service

        private final RestTemplate restTemplate = new RestTemplate();

        // Display list of quizzes
        @GetMapping("/quizzes")
        public String listQuizzes(Model model, HttpSession session) {
                try {
                        ResponseEntity<String[]> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes", String[].class);
                        model.addAttribute("quizzes", response.getBody());
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load quizzes.");
                }
                return "quiz-list"; // name of the Thymeleaf template for quiz list
        }

        // Show the form to create a quiz (only for ADMIN users)
        @GetMapping("/createquiz")
        public String createQuizForm(Model model, HttpSession session) {
                Object userObj = session.getAttribute("loggedInUser");

                if (userObj == null) {
                        return "redirect:/login";
                }

                User user = (User) userObj;

                if (!"ADMIN".equals(user.getRole())) {
                        model.addAttribute("error", "Only admins can create quizzes.");
                        model.addAttribute("loggedInUser", user);
                        return "create-quiz"; // still return view, but with warning
                }

                model.addAttribute("quiz", new Quiz());
                model.addAttribute("loggedInUser", user);
                return "create-quiz";
        }

        private QuizDTO convertToDTO(Quiz quiz) {
                QuizDTO dto = new QuizDTO();
                dto.setId(quiz.getId());
                dto.setTitle(quiz.getTitle());
                dto.setTimeLeft(quiz.getTimeLeft());
                dto.setDueDate(quiz.getDueDate());
                dto.setQuestions(quiz.getQuestions());
                dto.setTeacherAnswers(quiz.getTeacherAnswers());
                return dto;
        }

        // Handle submission of the create quiz form
        @PostMapping("/createquiz")
        public String createQuiz(@ModelAttribute Quiz quiz, Model model, HttpSession session) {
                // Only allow ADMIN users to create a quiz
                User user = (User) session.getAttribute("loggedInUser");

                if (user == null || !"ADMIN".equals(user.getRole())) {
                        model.addAttribute("error", "You do not have permission to create quizzes.");
                        return "create-quiz";
                }
                try {
                        Map<Integer, String> questions = new HashMap<>();
                        Map<Integer, String> answers = new HashMap<>();

                        String[] questionsArray = quiz.getQuestionsText().split("\\r?\\n");
                        String[] answersArray = quiz.getTeacherAnswersText().split("\\r?\\n");

                        for (int i = 0; i < questionsArray.length; i++) {
                                questions.put(i + 1, questionsArray[i]);
                        }

                        for (int i = 0; i < answersArray.length; i++) {
                                answers.put(i + 1, answersArray[i]);
                        }

                        quiz.setQuestions(questions);
                        quiz.setTeacherAnswers(answers);
                        // Prepare the request entity
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        QuizDTO quizDTO = convertToDTO(quiz);
                        HttpEntity<QuizDTO> request = new HttpEntity<>(quizDTO, headers);

                        // The quiz service expects JSON payload for the quiz creation
                        ResponseEntity<Quiz> response = restTemplate.postForEntity(
                                        quizServiceUrl + "/quizzes", request, Quiz.class);

                        // If successfully created, redirect to quiz list or details page
                        System.out.println(response);
                        return "redirect:/quizzes";
                } catch (HttpClientErrorException e) {
                        model.addAttribute("error", "Error creating quiz: " + e.getMessage());
                        return "create-quiz";
                }
        }
}
