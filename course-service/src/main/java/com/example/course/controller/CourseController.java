package com.example.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.course.model.Course;
import com.example.course.service.CourseService;

import java.util.*;

@RestController
public class CourseController {

    @Autowired
    private final CourseService moduleService;

    public CourseController(CourseService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/modules")
    public ResponseEntity<Course> createModule(@RequestBody Course module) {
        return new ResponseEntity<>(moduleService.createModule(module), HttpStatus.CREATED);
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<Course> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @GetMapping("/modules")
    public List<Course> getAllModules() {
        return moduleService.getAllModules();
    }

    @GetMapping("/modules/teacher/{teacherId}")
    public List<Course> getModulesByTeacherId(@PathVariable Long teacherId) {
        return moduleService.getModulesByTeacherId(teacherId);
    }
}