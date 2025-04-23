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
        dto.setOptions(quiz.getOptions());
        return dto;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<CollectionModel<EntityModel<QuizDTO>>> getQuizzes(
            @RequestParam(value = "courseId", required = false) Long courseId) {

        List<Quiz> quizzes = (courseId != null) ? quizService.findByCourseId(courseId) : quizService.getAllQuizzes();

        List<EntityModel<QuizDTO>> quizModels = quizzes.stream()
                .map(this::toDTO)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(quizModels,
                        linkTo(methodOn(QuizController.class).getQuizzes(courseId)).withSelfRel()));
    }

    @GetMapping(value = "/quizzes/{id}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<QuizDTO>> getQuiz(@PathVariable Integer id) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null)
            return ResponseEntity.notFound().build();
        System.out.println("QUIZ FOUND " + quiz);
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
        quiz.setOptions(quizDTO.getOptions());
        quizService.createQuiz(quiz);
        System.out.println("QUIZ SAVED"+ quiz);
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

    @GetMapping("/quizzes/{quizId}/submissions/{studentId}")
    public ResponseEntity<?> getOrStartSubmission(@PathVariable Integer quizId, @PathVariable Long studentId) {
        Optional<QuizSubmission> existing = quizService.findSubmission(studentId, quizId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(new QuizSubmissionDTO(
                    existing.get().getId(),
                    existing.get().getStudentId(),
                    existing.get().getQuizId(),
                    existing.get().getStudentAnswers()));
        }

        Quiz quiz = quizService.findById(quizId);
        if (quiz == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(toDTO(quiz));
    }

    @PostMapping("/quizzes/{quizId}/submissions")
    public ResponseEntity<QuizSubmissionDTO> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmissionDTO quizSubmissionDTO) {

        if (!quizId.equals(quizSubmissionDTO.getQuizId().longValue())) {
            return ResponseEntity.badRequest().body(null);
        }

        QuizSubmission saved = quizService.submit(quizSubmissionDTO.toEntity());
        QuizSubmissionDTO result = new QuizSubmissionDTO(
                saved.getId(),
                saved.getStudentId(),
                saved.getQuizId(),
                saved.getStudentAnswers());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
