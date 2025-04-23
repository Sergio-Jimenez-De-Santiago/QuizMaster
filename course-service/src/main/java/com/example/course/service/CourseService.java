package com.example.course.service;

import com.example.course.dto.CourseWithQuizzesDTO;
import com.example.course.dto.QuizDTO;
import com.example.course.model.Course;
import com.example.course.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CourseService(CourseRepository courseRepository, RestTemplate restTemplate) {
        this.courseRepository = courseRepository;
        this.restTemplate = restTemplate;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id " + id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    public void deleteCourse(long Id) {
        courseRepository.deleteById(Id);
    }
    public CourseWithQuizzesDTO getCourseWithQuizzes(Long courseId) {
        Course course = getCourseById(courseId);

        String quizServiceUrl = "http://quiz-service/quizzes?courseId=" + courseId;
        QuizDTO[] quizArray = restTemplate.getForObject(quizServiceUrl, QuizDTO[].class);

        CourseWithQuizzesDTO dto = new CourseWithQuizzesDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setTeacherId(course.getTeacherId());
        dto.setQuizzes(quizArray != null ? Arrays.asList(quizArray) : List.of());

        return dto;
    }
}
