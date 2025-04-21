package com.example.quiz.controller;

import com.example.quiz.dto.QuizDTO;
import com.example.quiz.dto.QuizSubmissionDTO;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizSubmission;
import com.example.quiz.service.QuizService;
import com.example.quiz.assembler.QuizModelAssembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class QuizController {

    private final QuizService quizService;
    private final QuizModelAssembler assembler;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    public QuizController(QuizService quizService, QuizModelAssembler assembler) {
        this.quizService = quizService;
        this.assembler = assembler;
    }

    private QuizDTO toDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setTimeLeft(quiz.getTimeLeft());
        dto.setCourseId(quiz.getCourseId());
        dto.setQuestions(quiz.getQuestions());
        dto.setTeacherAnswers(quiz.getTeacherAnswers());
        return dto;
    }

    @GetMapping(value = "/quizzes", produces = "application/hal+json")
    public ResponseEntity<CollectionModel<EntityModel<QuizDTO>>> getQuizzes() {
        List<EntityModel<QuizDTO>> quizzes = quizService.getAllQuizzes().stream()
                .map(this::toDTO)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                quizzes,
                linkTo(methodOn(QuizController.class).getQuizzes()).withSelfRel()
        ));
    }

    @GetMapping(value = "/quizzes/{id}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<QuizDTO>> getQuiz(@PathVariable Integer id) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(assembler.toModel(toDTO(quiz)));
    }

    @PostMapping(value = "/courses/{courseId}/quizzes", consumes = "application/json", produces = "application/hal+json")
    public ResponseEntity<EntityModel<QuizDTO>> createQuiz(@PathVariable Long courseId, @RequestBody QuizDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setQuestions(quizDTO.getQuestions());
        quiz.setTimeLeft(quizDTO.getTimeLeft());
        quiz.setTeacherAnswers(quizDTO.getTeacherAnswers());
        quiz.setCourseId(courseId);
        quizService.createQuiz(quiz);

        QuizDTO savedDTO = toDTO(quiz);
        EntityModel<QuizDTO> model = assembler.toModel(savedDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(model);
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable int id) {
        if (quizService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quizzes/byCourse/{courseId}")
    public ResponseEntity<CollectionModel<EntityModel<QuizDTO>>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<EntityModel<QuizDTO>> quizzes = quizService.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                quizzes,
                linkTo(methodOn(QuizController.class).getQuizzesByCourse(courseId)).withSelfRel()
        ));
    }

    @GetMapping("/quizzes/{id}/start")
    public ResponseEntity<?> startQuiz(@PathVariable Integer id, @RequestParam Long studentId) {
        Optional<QuizSubmission> existing = quizService.findSubmission(studentId, id);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing);
        }
        Quiz quiz = quizService.findById(id);
        if (quiz == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found");
        return ResponseEntity.ok(toDTO(quiz));
    }

    @PostMapping("/quizzes/{id}/submit")
    public ResponseEntity<String> submitQuiz(@RequestBody QuizSubmissionDTO quizSubmissionDTO) {
        QuizSubmission submission = new QuizSubmission();
        submission.setQuizId(quizSubmissionDTO.getQuizId());
        submission.setStudentId(quizSubmissionDTO.getStudentId());
        submission.setStudentAnswers(quizSubmissionDTO.getStudentAnswers());
        quizService.submit(submission);
        return ResponseEntity.ok("Quiz attempt submitted successfully.");
    }
}
