package com.example.frontend.controller;

import com.example.frontend.dto.GradeRequestDTO;
import com.example.frontend.dto.QuizDTO;
import com.example.frontend.dto.QuizSubmissionDTO;
import com.example.frontend.dto.UserProfileDTO;
import com.example.frontend.model.Course;
import com.example.frontend.model.Grade;
import com.example.frontend.model.Quiz;
import com.example.frontend.model.QuizSubmission;
import com.example.frontend.model.User;
import com.example.frontend.security.UserRole;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        @Value("${grading.service.url}")
        private String gradingServiceUrl;
        @Value("${course.service.url}")
        private String courseServiceUrl;

        private final RestTemplate restTemplate = new RestTemplate();

        @GetMapping("/quizzes")
        public String listQuizzes(Model model, HttpSession session) {
                try {
                        ResponseEntity<Quiz[]> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes", Quiz[].class);
                        model.addAttribute("quizzes", response.getBody());
                        UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                        model.addAttribute("loggedInUser", loggedInUser);
                        model.addAttribute("isStudent", loggedInUser != null && loggedInUser.getRole() == UserRole.STUDENT);
                        System.out.println("got quiz" + response.getBody());
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load quizzes.");
                }
                return "quiz-list";
        }

        @GetMapping("/quizzes/{id}")
        public String getQuiz(@PathVariable Integer id, Model model, HttpSession session) {
                // Get loggedInUser through the HttpSession
                UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                if (loggedInUser == null) {
                        return "redirect:/login";
                }
                model.addAttribute("loggedInUser", loggedInUser);

                Quiz quiz = null;
                try {
                        ResponseEntity<Quiz> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        quiz = response.getBody();
                        model.addAttribute("quiz", quiz);
                        System.out.println("Got quiz with id: " + id);
                        System.out.println(quiz);
                } catch (Exception e) {
                        model.addAttribute("error", "Could not load the quiz.");
                        return "quiz-detail";
                }

                // Get the course associated with the quiz
                Course course = null;
                try {
                        ResponseEntity<Course> courseResponse = restTemplate.getForEntity(
                                        courseServiceUrl + "/courses/" + quiz.getCourseId(), Course.class);
                        course = courseResponse.getBody();
                } catch (Exception e) {
                        model.addAttribute("error", "Could not determine course ownership.");
                        return "quiz-detail";
                }

                // Check if this user is the owner of the course
                boolean isOwner = loggedInUser.getRole() == UserRole.TEACHER
                                && course.getTeacherId().equals(loggedInUser.getId());
                boolean isStudent = loggedInUser.getRole() == UserRole.STUDENT || !isOwner;

                model.addAttribute("isOwner", isOwner);
                model.addAttribute("isStudent", isStudent);

                // Get submission info for students only
                if (loggedInUser.getRole() == UserRole.STUDENT) {
                        try {
                                QuizSubmission submissionResponse = restTemplate.getForObject(
                                                quizServiceUrl + "/quizzes/" + id + "/submissions/"
                                                                + loggedInUser.getId(),
                                                QuizSubmission.class);

                                model.addAttribute("submission", submissionResponse.getStudentAnswers() != null);
                        } catch (Exception e) {
                                model.addAttribute("submission", false);
                        }
                } else {
                        model.addAttribute("submission", false);
                }

                // Grade (students only)
                if (loggedInUser.getRole() == UserRole.STUDENT) {
                        try {
                                String gradeUrl = gradingServiceUrl + "/grades?studentId=" + loggedInUser.getId()
                                                + "&quizId=" + id;
                                ResponseEntity<Grade[]> gradeResponse = restTemplate.getForEntity(gradeUrl,
                                                Grade[].class);
                                Grade[] grades = gradeResponse.getBody();

                                if (grades != null && grades.length > 0) {
                                        Grade grade = grades[0];
                                        model.addAttribute("grade", grade.getScore());
                                        System.out.println("Found grade: " + grade.getScore());
                                }
                        } catch (Exception e) {
                                System.out.println("Grade not available yet: " + e.getMessage());
                        }
                }

                return "quiz-detail";
        }

        @GetMapping("/quizzes/{id}/start")
        public String startQuiz(@PathVariable Integer id, Model model, HttpSession session) {
                UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                if (loggedInUser == null) {
                        return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("isStudent", loggedInUser.getRole() == UserRole.STUDENT);

                try {
                        ResponseEntity<Quiz> response = restTemplate.getForEntity(
                                        quizServiceUrl + "/quizzes/" + id, Quiz.class);
                        model.addAttribute("quiz", response.getBody());

                        QuizSubmission submissionResponse = restTemplate.getForObject(
                                        quizServiceUrl + "/quizzes/" + id + "/submissions/" + loggedInUser.getId(),
                                        QuizSubmission.class);

                        if (submissionResponse.getStudentAnswers() != null) {
                                model.addAttribute("submission", submissionResponse);
                                model.addAttribute("quiz", response.getBody());
                                return "submission-result";
                        }

                        System.out.println(response.getBody());
                        model.addAttribute("timeLeft", response.getBody().getTimeLeft());
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
                UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
                        return "redirect:/login";
                }

                Quiz quiz = new Quiz();
                quiz.setCourseId(courseId);
                quiz.setQuestions(new HashMap<>());
                quiz.setOptions(new HashMap<>());
                quiz.setTeacherAnswers(new HashMap<>());

                int first = 1;
                quiz.getQuestions().put(first, "");
                quiz.getOptions().put(first, List.of("a", "b", "c"));
                quiz.getTeacherAnswers().put(first, "");
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
        public String createQuiz(@ModelAttribute Quiz quiz,
                        Model model,
                        HttpSession session) {
                UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                if (loggedInUser == null || loggedInUser.getRole() != UserRole.TEACHER) {
                        return "redirect:/login";
                }

                if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty() ||
                                quiz.getTimeLeft() == null || quiz.getTimeLeft().trim().isEmpty() ||
                                quiz.getQuestions() == null || quiz.getQuestions().isEmpty() ||
                                quiz.getOptions() == null || quiz.getOptions().isEmpty() ||
                                quiz.getTeacherAnswers()== null|| quiz.getTeacherAnswers().isEmpty()
                                ) {
                        model.addAttribute("error",
                                        "Please fill in a title, time,at least one question with options and a correct answer selected");
                        return "create-quiz";
                }

                int time;
                try {
                        time = Integer.parseInt(quiz.getTimeLeft().trim());
                        if (time <= 0 || time > 600) {
                                model.addAttribute("error", "Time must be between 1 and 600 minutes.");
                                return "create-quiz";
                        }
                } catch (NumberFormatException ex) {
                        model.addAttribute("error", "Time must be a valid integer.");
                        return "create-quiz";
                }

                Map<Integer, String> questions = quiz.getQuestions();
                Map<Integer, List<String>> options = quiz.getOptions();
                Map<Integer, String> teacherAnswers = quiz.getTeacherAnswers();

                for (Integer qNo : questions.keySet()) {
                        String qText = questions.get(qNo);
                        if (qText == null || qText.trim().isEmpty()) {
                                model.addAttribute("error", "Question " + qNo + " must not be empty.");
                                return "create-quiz";
                        }

                        List<String> opts = options.get(qNo);
                        if (opts == null || opts.size() != 3 ||
                                        opts.stream().anyMatch(o -> o == null || o.trim().isEmpty())) {
                                model.addAttribute("error",
                                                "Question " + qNo + " must have exactly 3 non-empty options.");
                                return "create-quiz";
                        }

                        String correct = teacherAnswers.get(qNo);
                        if (correct == null || !opts.contains(correct)) {
                                model.addAttribute("error",
                                                "For question " + qNo
                                                                + ", you must select one of the three options as correct.");
                                return "create-quiz";
                        }
                }

                try {
                        QuizDTO dto = convertToDTO(quiz);
                        dto.setOptions(options);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<QuizDTO> request = new HttpEntity<>(dto, headers);

                        restTemplate.postForEntity(
                                        quizServiceUrl + "/courses/" + quiz.getCourseId() + "/quizzes",
                                        request,
                                        Quiz.class);

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

                UserProfileDTO loggedInUser = (UserProfileDTO) session.getAttribute("loggedInUser");
                if (loggedInUser == null) {
                        System.err.println("No logged-in user. Redirecting to login.");
                        return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("isStudent", loggedInUser.getRole() == UserRole.STUDENT);

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

                try {
                        // Submit quiz
                        String submitUrl = quizServiceUrl + "/quizzes/" + id + "/submissions";
                        QuizSubmissionDTO submissionDTO = new QuizSubmissionDTO();
                        submissionDTO.setQuizId(id);
                        submissionDTO.setStudentAnswers(studentAnswers);
                        submissionDTO.setStudentId(loggedInUser.getId());

                        System.out.println("Sending quiz submission to " + submitUrl);
                        ResponseEntity<QuizSubmissionDTO> response = restTemplate.postForEntity(
                                        submitUrl, submissionDTO, QuizSubmissionDTO.class);

                        QuizSubmissionDTO submission = response.getBody();
                        System.out.println("Quiz submitted. Response: " + submission);

                        // Fetch quiz
                        String quizUrl = quizServiceUrl + "/quizzes/" + id;
                        ResponseEntity<Quiz> quizResponse = restTemplate.getForEntity(quizUrl, Quiz.class);
                        Quiz quiz = quizResponse.getBody();

                        System.out.println("Fetched quiz: " + quiz);
                        if (quiz == null) {
                                System.err.println("❌ Quiz was null.");
                                return "redirect:/courses";
                        }

                        Map<Integer, String> correctAnswers = quiz.getTeacherAnswers();
                        if (correctAnswers == null || correctAnswers.isEmpty()) {
                                System.err.println("❌ teacherAnswers is missing — skipping grading.");
                                return "redirect:/courses";
                        }

                        System.out.println("Preparing grading request...");
                        GradeRequestDTO gradeRequest = new GradeRequestDTO();
                        gradeRequest.setStudentId(loggedInUser.getId());
                        gradeRequest.setQuizId(id.longValue());
                        gradeRequest.setStudentAnswers(studentAnswers);
                        gradeRequest.setCorrectAnswers(correctAnswers);

                        // Call grading service
                        try {
                                String gradingUrl = gradingServiceUrl + "/grades";
                                System.out.println("Calling grading service at: " + gradingUrl);
                                ResponseEntity<Grade> gradeResponse = restTemplate.postForEntity(gradingUrl,
                                                gradeRequest, Grade.class);
                                System.out.println("Grading result: " + gradeResponse.getBody());
                        } catch (Exception e) {
                                System.err.println("❗ Grading failed: ");
                                e.printStackTrace();
                        }

                        model.addAttribute("message", "Quiz submitted successfully!");
                        return "redirect:/courses/" + quiz.getCourseId();

                } catch (Exception e) {
                        System.err.println("❗ Quiz submission failed: ");
                        e.printStackTrace();
                        model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
                        return "redirect:/courses";
                }
        }

}
