package com.example.quiz.controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz.dto.QuizDTO;
import com.example.quiz.dto.QuizSubmissionDTO;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizSubmission;
import com.example.quiz.service.QuizService;
import com.example.quiz.service.QuizSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.net.UnknownHostException;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class quizController {

    @Autowired
    private final QuizService quizService;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    public quizController(QuizService quizService) {
        this.quizService = quizService;
    }

    private String getHost() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            return "%s:%d".formatted(host.getHostAddress(),
                    serverPort);
        } catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }
        return "";
    }

    @GetMapping(value = "/quizzes", produces = "application/json")
    public ResponseEntity<List<Quiz>> getQuizzes() {
        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            System.out.println("got quizzes " + quizzes);
            return ResponseEntity.status(HttpStatus.OK).body(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/quizzes/{id}", produces = { "application/json" })
    public ResponseEntity<Quiz> getQuiz(
            @PathVariable Integer id) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(quiz);
    }
    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable int id) {
        quizService.deleteQuiz(id);
        if (quizService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping(value = "/quizzes", consumes = "application/json")
    public ResponseEntity<Quiz> createQuiz(
            @RequestBody QuizDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setQuestions(quizDTO.getQuestions());
        quiz.setTimeLeft(quizDTO.getTimeLeft());
        quiz.setTeacherAnswers(quizDTO.getTeacherAnswers());
        quizService.createQuiz(quiz);
        String url = "http://" + getHost() + "/quizzes/"
                + quiz.getTitle();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(quiz);
    }

    @GetMapping("/quizzes/{id}/start")
    public ResponseEntity<?> startQuiz(@PathVariable Integer id, @RequestParam Long studentId) {
        Optional<QuizSubmission> existingSubmission = quizService.findSubmission(studentId, id);

        if (existingSubmission.isPresent()) {
            return ResponseEntity.ok(existingSubmission); 
        }

        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found");
        }

        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/quizzes/{id}/submit")
    public ResponseEntity<Object> submitQuiz(
            @RequestBody QuizSubmissionDTO quizSubmissionDTO) {

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuizId(quizSubmissionDTO.getQuizId());
        quizSubmission.setStudentId(quizSubmissionDTO.getStudentId());
        quizSubmission.setStudentAnswers(quizSubmissionDTO.getStudentAnswers());
        quizService.submit(quizSubmission);
        return ResponseEntity.ok("Quiz attempt submitted successfully.");

    }

    @GetMapping("/quizzes/byCourse/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<Quiz> quizzes = quizService.findByCourseId(courseId);
        List<QuizDTO> quizDTOs = quizzes.stream().map(quiz -> {
            QuizDTO dto = new QuizDTO();
            dto.setId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setTimeLeft(quiz.getTimeLeft());
            dto.setCourseId(quiz.getCourseId());
            return dto;
        }).toList();

        return ResponseEntity.ok(quizDTOs);
    }


}