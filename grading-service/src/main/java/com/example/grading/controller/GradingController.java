package com.example.grading.controller;

import com.example.grading.model.Grade;
import com.example.grading.service.GradingService;
import com.example.grading.dto.GradeRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradingController {

    private final GradingService gradingService;

    @Autowired
    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    @PostMapping
    public ResponseEntity<Grade> gradeQuiz(@RequestBody GradeRequestDTO requestDTO) {
        Grade grade = gradingService.calculateAndSaveGrade(requestDTO);
        return new ResponseEntity<>(grade, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradingService.getAllGrades();
        return new ResponseEntity<>(grades, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Grade grade = gradingService.getGradeById(id);
        if (grade != null) {
            return new ResponseEntity<>(grade, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        List<Grade> grades = gradingService.getGradesByStudentId(studentId);
        return new ResponseEntity<>(grades, HttpStatus.OK);
    }
    
    @GetMapping("/student/{studentId}/quiz/{quizId}")
    public ResponseEntity<Grade> getGradeByStudentIdAndQuizId(
            @PathVariable Long studentId,
            @PathVariable Long quizId) {
        Optional<Grade> gradeOpt = gradingService.getGradesByStudentIdAndQuizId(studentId, quizId);
        return gradeOpt.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
