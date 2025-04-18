package com.example.enrolment.controller;

import com.example.user.assembler.UserDTOModelAssembler;
import com.example.user.model.User;
import com.example.user.dto.UserProfileDTO;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnrolmentController {

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

    @GetMapping("/enrolments/module/{moduleId}")
    public List<Enrolment> getEnrolmentsByModuleId(@PathVariable Long moduleId) {
        return enrolmentService.getEnrolmentsByModuleId(moduleId);
    }
}
