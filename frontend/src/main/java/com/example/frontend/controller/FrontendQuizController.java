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
                        ResponseEntity<Quiz[]> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes", Quiz[].class);
                        model.addAttribute("quizzes", response.getBody());
                        System.out.println("got quiz" + response.getBody());
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load quizzes.");
                }
                return "quiz-list";
        }

        @GetMapping("/quizzes/{id}")
        public String getQuiz(@PathVariable Integer id, Model model, HttpSession session) {
                try {
                        ResponseEntity<Quiz> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        model.addAttribute("quiz", response.getBody());
                        System.out.println("Got quiz with id: " + id);
                        System.out.println(response.getBody());
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load the quiz.");
                }
                Object userObj = session.getAttribute("loggedInUser");
                User user = (User) userObj;
                model.addAttribute("USER", user != null && "USER".equals(user.getRole()));
                System.out.println(user.getRole());
                model.addAttribute("loggedInUser", user);
                return "quiz-detail";
        }

        @GetMapping("/quizzes/{id}/start")
        public String startQuiz(@PathVariable Integer id, Model model) {
                try {
                        ResponseEntity<Quiz> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        model.addAttribute("quiz", response.getBody());
                        System.out.println(response.getBody());
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load the quiz.");
                }
                Map<Integer, String> studentAnswers = new HashMap<>();
                model.addAttribute("quizSession", studentAnswers);

                return "quiz-attempt";
        }

        @DeleteMapping("/quizzes/{id}")
        public String deleteQuiz(@PathVariable Integer id, Model model) {
                try {
                        restTemplate.delete(quizServiceUrl + "/quizzes/" + id);
                } catch (Exception e) {
                        model.addAttribute("error", "Could not delete the quiz.");
                }
                return "redirect:/quizzes";
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
                        System.out.println("created" + response);
                        return "redirect:/quizzes";
                } catch (HttpClientErrorException e) {
                        model.addAttribute("error", "Error creating quiz: " + e.getMessage());
                        return "create-quiz";
                }
        }

        @PostMapping("/submit-quiz/{id}")
        public String submitQuiz(@PathVariable Integer id,
                        @RequestParam Map<String, String> allParams,
                        HttpSession session,
                        Model model) {
                // Extract student answers from form parameters
                Map<Integer, String> studentAnswers = new HashMap<>();

                for (Map.Entry<String, String> entry : allParams.entrySet()) {
                        String key = entry.getKey();
                        if (key.startsWith("studentAnswers[")) {
                                // Extract question number from key
                                String indexStr = key.substring("studentAnswers[".length(), key.length() - 1);
                                try {
                                        int index = Integer.parseInt(indexStr);
                                        studentAnswers.put(index, entry.getValue());
                                } catch (NumberFormatException e) {
                                        System.out.println("Invalid key in form: " + key);
                                }
                        }
                }

                // Optionally: attach student user info
                User user = (User) session.getAttribute("loggedInUser");

                // Now send this info to the backend
                try {
                        String url = quizServiceUrl + "/quizzes/" + id + "/submit";

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        // This map contains the answers
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("studentAnswers", studentAnswers);
                        if (user != null) {
                                requestBody.put("studentId", user.getId()); // Optional, if needed
                        }

                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                        System.out.println("Submitted answers: " + studentAnswers);
                        System.out.println("Backend response: " + response.getBody());

                        model.addAttribute("message", "Quiz submitted successfully!");
                        return "redirect:/quizzes";
                } catch (Exception e) {
                        model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
                        return "quiz-attempt";
                }
        }

}
