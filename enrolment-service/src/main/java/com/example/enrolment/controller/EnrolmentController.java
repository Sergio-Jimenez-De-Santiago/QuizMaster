package com.example.enrolment.controller;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        URI location = linkTo(methodOn(EnrolmentController.class).getEnrolmentById(saved.getEnrolmentId())).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/enrolments")
    public List<Enrolment> getEnrolments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId) {

        if (studentId != null && courseId != null) {
            return enrolmentService.getEnrolmentsByStudentIdAndCourseId(studentId, courseId);
        } else if (studentId != null) {
            return enrolmentService.getEnrolmentsByStudentId(studentId);
        } else if (courseId != null) {
            return enrolmentService.getEnrolmentsByCourseId(courseId);
        } else {
            return enrolmentService.getAllEnrolments();
        }
    }

    @GetMapping("/enrolments/{id}")
    public ResponseEntity<Enrolment> getEnrolmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentById(id));
    }
    
    @GetMapping("courses/{courseId}/enrolments")
    public ResponseEntity<List<Enrolment>> getEnrolmentByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentsByCourseId(courseId));
    }

    @DeleteMapping("/enrolments/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (enrolmentService.getEnrolmentById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        enrolmentService.deleteEnrolment(id);
        return ResponseEntity.noContent().build();
    }
}
