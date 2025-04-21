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
        public String startQuiz(
                        @PathVariable Integer id,
                        Model model,
                        HttpSession session) {

                User user = (User) session.getAttribute("loggedInUser");
                if (user == null) {
                        return "redirect:/login";
                }

                try {
                        String startUrl = quizServiceUrl + "/quizzes/" + id + "/start?studentId=" + user.getId();

                        ResponseEntity<Map> response = restTemplate.getForEntity(startUrl, Map.class);
                        Map<String, Object> payload = response.getBody();

                        Quiz quiz = restTemplate.getForObject(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);

                        model.addAttribute("quiz", quiz);
                        model.addAttribute("timeLeft", quiz.getTimeLeft());

                        if (payload != null && payload.containsKey("studentAnswers")) {
                                QuizSubmission submission = restTemplate.getForObject(
                                                startUrl, QuizSubmission.class);

                                model.addAttribute("submission", submission);
                                return "submission-result";
                        } else {
                                model.addAttribute("quizSession", new HashMap<Integer, String>());
                                return "quiz-attempt";
                        }

                } catch (Exception e) {
                        model.addAttribute("error", "Error loading quiz: " + e.getMessage());
                        return "redirect:/quizzes";
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
                                return "redirect:/quizzes";
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
                        return "redirect:/quizzes";
                }
        }

        // Show the form to create a quiz (only for TEACHER users)
        @GetMapping("/courses/{courseId}/create-quiz")
        public String createQuizForm(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
                // Get loggedInUser through the HttpSession
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                if (loggedInUser == null || !"TEACHER".equalsIgnoreCase(loggedInUser.getRole())) {
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
                if (loggedInUser == null || !"TEACHER".equalsIgnoreCase(loggedInUser.getRole())) {
                        return "redirect:/login";
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
