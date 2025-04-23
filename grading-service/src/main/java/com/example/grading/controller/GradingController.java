package com.example.grading.controller;

import com.example.grading.model.Grade;
import com.example.grading.service.GradingService;
import com.example.grading.dto.GradeRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GradingController {

    private final GradingService gradingService;

    @Autowired
    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    @PostMapping("/grades")
    public ResponseEntity<Grade> evaluateAndStoreGrade(@RequestBody GradeRequestDTO requestDTO) {
        Grade grade = gradingService.calculateAndSaveGrade(requestDTO);
        return new ResponseEntity<>(grade, HttpStatus.CREATED);
    }

    @GetMapping("/grades")
    public ResponseEntity<List<Grade>> getGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long quizId) {

        if (studentId != null && quizId != null) {
            return gradingService.getGradesByStudentIdAndQuizId(studentId, quizId)
                    .map(grade -> ResponseEntity.ok(List.of(grade)))
                    .orElse(ResponseEntity.notFound().build());
        }

        if (studentId != null) {
            List<Grade> grades = gradingService.getGradesByStudentId(studentId);
            return ResponseEntity.ok(grades);
        }

        // default: return all grades
        List<Grade> all = gradingService.getAllGrades();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/grades/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Grade grade = gradingService.getGradeById(id);
        if (grade != null) {
            return new ResponseEntity<>(grade, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
