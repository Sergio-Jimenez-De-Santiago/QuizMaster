package com.example.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.course.dto.CourseWithQuizzesDTO;
import com.example.course.model.Course;
import com.example.course.service.CourseService;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CourseController {

    @Autowired
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course created = courseService.createCourse(course);
        URI location = linkTo(methodOn(CourseController.class).getCourseById(created.getId())).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/courses")
    public List<Course> getCourses(@RequestParam(required = false) Long teacherId) {
        return teacherId != null ? courseService.getCoursesByTeacherId(teacherId) : courseService.getAllCourses();
    }

    // Returns a course enriched with a list of its related quizzes (view projection)
    @GetMapping("/courses/{id}/with-quizzes")
    public ResponseEntity<CourseWithQuizzesDTO> getCourseWithQuizzes(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithQuizzes(id));
    }
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.getCourseById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}