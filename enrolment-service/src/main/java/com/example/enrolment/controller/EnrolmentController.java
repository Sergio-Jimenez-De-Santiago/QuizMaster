package com.example.enrolment.controller;


import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.enrolment.model.Enrolment;
import com.example.enrolment.service.EnrolmentService;
@RestController
public class EnrolmentController {

    @Autowired
    private final EnrolmentService enrolmentService;

    public EnrolmentController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    @PostMapping("/enrolments")
    public ResponseEntity<Enrolment> createEnrolment(@RequestBody @Valid Enrolment enrolment) {
        Enrolment saved = enrolmentService.createEnrolment(enrolment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/enrolments")
    public List<Enrolment> getAllEnrolments() {
        return enrolmentService.getAllEnrolments();
    }

    @GetMapping("/enrolments/{id}")
    public ResponseEntity<Enrolment> getEnrolmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentById(id));
    }

    @GetMapping("/enrolments/student/{studentId}")
    public List<Enrolment> getEnrolmentsByStudentId(@PathVariable Long studentId) {
        return enrolmentService.getEnrolmentsByStudentId(studentId);
    }

    @GetMapping("/enrolments/course/{courseId}")
    public List<Enrolment> getEnrolmentsByCourseId(@PathVariable Long courseId) {
        return enrolmentService.getEnrolmentsByCourseId(courseId);
    }
}
