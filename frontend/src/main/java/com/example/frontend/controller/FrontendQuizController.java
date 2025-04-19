package com.example.frontend.controller;

import com.example.frontend.dto.QuizDTO;
import com.example.frontend.dto.QuizSubmissionDTO;
import com.example.frontend.model.Quiz;
import com.example.frontend.model.QuizSubmission;
import com.example.frontend.model.User;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
        private String quizServiceUrl;

        private final RestTemplate restTemplate = new RestTemplate();

        @GetMapping("/quizzes")
        public String listQuizzes(Model model, HttpSession session) {
                try {
                        ResponseEntity<Quiz[]> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes", Quiz[].class);
                        model.addAttribute("quizzes", response.getBody());
                        Object userObj = session.getAttribute("loggedInUser");
                        User user = (User) userObj;
                        model.addAttribute("STUDENT", user != null && "STUDENT".equals(user.getRole()));
                        model.addAttribute("loggedInUser", user);
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
                model.addAttribute("STUDENT", user != null && "STUDENT".equals(user.getRole()));
                System.out.println(user.getRole());
                model.addAttribute("loggedInUser", user);
                return "quiz-detail";
        }

        @GetMapping("/quizzes/{id}/start")
        public String startQuiz(@PathVariable Integer id, Model model, HttpSession session) {
                User user = (User) session.getAttribute("loggedInUser");

                if (user == null) {
                        return "redirect:/login";
                }

                try {
                        String url = quizServiceUrl + "/quizzes/" + id + "/start?studentId=" + user.getId();

                        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
                        Object body = response.getBody();

                        if (body instanceof LinkedHashMap map) {
                                if (map.containsKey("studentAnswers")) {
                                        Integer quizId = (Integer) map.get("quizId");

                                        ResponseEntity<Quiz> quizResponse = restTemplate.getForEntity(
                                                        quizServiceUrl + "/quizzes/" + quizId, Quiz.class);
                                        Quiz quiz = quizResponse.getBody();
                                        ResponseEntity<QuizSubmission> quizSubmisssionResponse = restTemplate
                                                        .getForEntity(url, QuizSubmission.class);
                                        QuizSubmission submission = quizSubmisssionResponse.getBody();
                                        model.addAttribute("submission", submission);
                                        model.addAttribute("quiz", quiz);
                                        return "submission-result";
                                } else if (map.containsKey("questions")) {
                                        // It's a quiz attempt
                                        model.addAttribute("quiz", map);
                                        model.addAttribute("quizSession", new HashMap<Integer, String>());
                                        return "quiz-attempt";
                                }
                        }

                        model.addAttribute("error", "Unexpected response format.");
                        return "redirect:/quizzes";

                } catch (Exception e) {
                        model.addAttribute("error", "Error loading quiz: " + e.getMessage());
                        return "redirect:/quizzes";
                }
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

        // Show the form to create a quiz (only for TEACHER users)
        @GetMapping("/createquiz")
        public String createQuizForm(Model model, HttpSession session) {
                Object userObj = session.getAttribute("loggedInUser");

                if (userObj == null) {
                        return "redirect:/login";
                }

                User user = (User) userObj;

                if (!"TEACHER".equals(user.getRole())) {
                        model.addAttribute("error", "Only teachers can create quizzes.");
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
                // Only allow TEACHER users to create a quiz
                User user = (User) session.getAttribute("loggedInUser");

                if (user == null || !"TEACHER".equals(user.getRole())) {
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
                Map<Integer, String> studentAnswers = new HashMap<>();

                for (Map.Entry<String, String> entry : allParams.entrySet()) {
                        String key = entry.getKey();
                        if (key.startsWith("studentAnswers[")) {
                                String indexStr = key.substring("studentAnswers[".length(), key.length() - 1);
                                try {
                                        int index = Integer.parseInt(indexStr);
                                        studentAnswers.put(index, entry.getValue());
                                } catch (NumberFormatException e) {
                                        System.out.println("Invalid key in form: " + key);
                                }
                        }
                }

                User user = (User) session.getAttribute("loggedInUser");

                try {
                        String url = quizServiceUrl + "/quizzes/" + id + "/submit";

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        QuizSubmissionDTO submissionDTO = new QuizSubmissionDTO();
                        submissionDTO.setQuizId(id);
                        submissionDTO.setStudentAnswers(studentAnswers);
                        if (user != null) {
                                submissionDTO.setStudentId((user.getId()));
                        }

                        HttpEntity<QuizSubmissionDTO> request = new HttpEntity<>(submissionDTO, headers);

                        ResponseEntity<QuizSubmissionDTO> response = restTemplate.postForEntity(
                                        url, request, QuizSubmissionDTO.class);

                        QuizSubmissionDTO submission = response.getBody();

                        System.out.println("Submitted answers: " + studentAnswers);
                        System.out.println("Received submission DTO: " + submission);

                        model.addAttribute("message", "Quiz submitted successfully!");

                        return "redirect:/quizzes";
                } catch (Exception e) {
                        model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
                        return "redirect:/quizzes";
                }
        }

        @GetMapping("/quiz-list")
        public String showQuizList(Model model, HttpSession session) {
                User user = (User) session.getAttribute("loggedInUser");
                model.addAttribute("loggedInUser", user);
                model.addAttribute("teacher", user != null && "TEACHER".equals(user.getRole()));
                return "quiz-list";
        }

}
