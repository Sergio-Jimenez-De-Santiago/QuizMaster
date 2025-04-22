package com.example.frontend.controller;

import com.example.frontend.dto.QuizDTO;
import com.example.frontend.dto.QuizSubmissionDTO;
import com.example.frontend.model.Quiz;
import com.example.frontend.model.QuizSubmission;
import com.example.frontend.model.User;
import com.example.frontend.security.UserRole;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
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
                        model.addAttribute("loggedInUser", user);
                        model.addAttribute("isStudent", user != null && user.getRole() == UserRole.STUDENT);
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
                model.addAttribute("loggedInUser", user);
                model.addAttribute("isStudent", user != null && user.getRole() == UserRole.STUDENT);

                return "quiz-detail";
        }

        @GetMapping("/quizzes/{id}/start")
        public String startQuiz(
                        @PathVariable Integer id,
                        Model model,
                        HttpSession session) {

                User user = (User) session.getAttribute("loggedInUser");
                if (user == null) {
                        return "redirect:/login";
                }

                model.addAttribute("loggedInUser", user);
                model.addAttribute("isStudent", user.getRole() == UserRole.STUDENT);

                try {
                        String startUrl = quizServiceUrl + "/quizzes/" + id + "/start?studentId=" + user.getId();

                        // Try to get submission
                        ResponseEntity<QuizSubmission> submissionResponse = restTemplate.exchange(
                                        startUrl,
                                        HttpMethod.GET,
                                        null,
                                        new ParameterizedTypeReference<QuizSubmission>() {
                                        });

                        if (submissionResponse.getStatusCode().is2xxSuccessful()
                                        && submissionResponse.getBody().getStudentAnswers() != null) {
                                model.addAttribute("submission", submissionResponse.getBody());
                                return "submission-result";
                        }

                        ResponseEntity<EntityModel<QuizDTO>> quizResponse = restTemplate.exchange(
                                        quizServiceUrl + "/quizzes/" + id,
                                        HttpMethod.GET,
                                        null,
                                        new ParameterizedTypeReference<EntityModel<QuizDTO>>() {
                                        });

                        QuizDTO quizDto = quizResponse.getBody().getContent();

                        model.addAttribute("quiz", quizDto);
                        model.addAttribute("timeLeft", quizDto.getTimeLeft());
                        model.addAttribute("quizSession", new HashMap<Integer, String>());

                        return "quiz-attempt";

                } catch (Exception e) {
                        model.addAttribute("error", "Error loading quiz: " + e.getMessage());
                        return "redirect:/courses";
                }
        }

        @DeleteMapping("/quizzes/{id}")
        public String deleteQuiz(@PathVariable Integer id, Model model, HttpSession session) {
                try {

                        ResponseEntity<Quiz> quizResponse = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        Quiz quiz = quizResponse.getBody();

                        if (quiz == null) {
                                model.addAttribute("error", "Quiz not found.");
                                return "redirect:/courses";
                        }

                        long courseId = quiz.getCourseId();

                        try {
                                restTemplate.delete(quizServiceUrl + "/quizzes/" + id);
                        } catch (HttpClientErrorException.NotFound e) {
                                model.addAttribute("error", "Quiz already deleted.");
                        }

                        return "redirect:/courses/" + courseId;

                } catch (Exception e) {
                        e.printStackTrace();
                        model.addAttribute("error", "Something went wrong trying to delete the quiz.");
                        return "redirect:/courses";
                }
        }

        // Show the form to create a quiz (only for TEACHER users)
        @GetMapping("/courses/{courseId}/create-quiz")
        public String createQuizForm(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
                // Get loggedInUser through the HttpSession
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
                        return "redirect:/login";
                }

                Quiz quiz = new Quiz();
                quiz.setCourseId(courseId);

                model.addAttribute("quiz", quiz);
                model.addAttribute("loggedInUser", loggedInUser);
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
        @PostMapping("/courses/{courseId}/create-quiz")
        public String createQuiz(@ModelAttribute Quiz quiz, Model model, HttpSession session) {
                // Get loggedInUser through the HttpSession
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
                        return "redirect:/login";
                }

                try {
                        // Validate non-empty fields
                        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty() ||
                                        quiz.getTimeLeft() == null || quiz.getTimeLeft().trim().isEmpty() ||
                                        quiz.getQuestionsText() == null || quiz.getQuestionsText().trim().isEmpty() ||
                                        quiz.getTeacherAnswersText() == null || quiz.getTeacherAnswersText().trim().isEmpty()) {

                                model.addAttribute("error",  "All fields must be filled in");
                                return "create-quiz";
                        }

                        // Validate timeLeft
                        int time;
                        try {
                                time = Integer.parseInt(quiz.getTimeLeft().trim());
                                if (time <= 0 || time > 600) {
                                        model.addAttribute("error",
                                                        "Time must be an integer between 1 and 600 (both included)");
                                        return "create-quiz";
                                }
                        } catch (NumberFormatException e) {
                                model.addAttribute("error", "Time must be a valid integer");
                                return "create-quiz";
                        }

                        String[] questionsArray = quiz.getQuestionsText().split("\\r?\\n");
                        String[] answersArray = quiz.getTeacherAnswersText().split("\\r?\\n");

                        List<String> filteredQuestions = Arrays.stream(questionsArray).map(String::trim)
                                        .filter(s -> !s.isEmpty()).toList();
                        List<String> filteredAnswers = Arrays.stream(answersArray).map(String::trim)
                                        .filter(s -> !s.isEmpty()).toList();

                        // Number of questions and answers has to match
                        if (filteredQuestions.size() != filteredAnswers.size()) {
                                model.addAttribute("error",
                                                "The number of non-empty questions and answers must be equal");
                                return "create-quiz";
                        }

                        Map<Integer, String> questions = new HashMap<>();
                        Map<Integer, String> answers = new HashMap<>();
                        for (int i = 0; i < filteredQuestions.size(); i++) {
                                questions.put(i + 1, filteredQuestions.get(i));
                        }
                        for (int i = 0; i < filteredAnswers.size(); i++) {
                                answers.put(i + 1, filteredAnswers.get(i));
                        }
                        quiz.setQuestions(questions);
                        quiz.setTeacherAnswers(answers);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        QuizDTO quizDTO = convertToDTO(quiz);
                        HttpEntity<QuizDTO> request = new HttpEntity<>(quizDTO, headers);

                        restTemplate.postForEntity(quizServiceUrl + "/courses/" + quiz.getCourseId() + "/quizzes",
                                        request, Quiz.class);

                        return "redirect:/courses/" + quiz.getCourseId();
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
                        ResponseEntity<Quiz> quizResponse = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        Quiz quiz = quizResponse.getBody();
                        long courseId = quiz.getCourseId();
                        return "redirect:/courses/" + courseId;
                } catch (Exception e) {
                        model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
                        return "redirect:/courses";
                }
        }

}
