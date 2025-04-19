package com.example.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.course.dto.CourseWithQuizzesDTO;
import com.example.course.model.Course;
import com.example.course.service.CourseService;

import java.util.*;

@RestController
public class CourseController {

    @Autowired
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return new ResponseEntity<>(courseService.createCourse(course), HttpStatus.CREATED);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/courses/teacher/{teacherId}")
    public List<Course> getCoursesByTeacherId(@PathVariable Long teacherId) {
        return courseService.getCoursesByTeacherId(teacherId);
    }

    @GetMapping("/courses/{id}/with-quizzes")
    public ResponseEntity<CourseWithQuizzesDTO> getCourseWithQuizzes(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithQuizzes(id));
    }

}