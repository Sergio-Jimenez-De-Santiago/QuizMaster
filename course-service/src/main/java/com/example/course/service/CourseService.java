package com.example.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.course.model.Course;
import com.example.course.repository.CourseRepository;

import jakarta.validation.Valid;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository moduleRepository;

    public CourseService(CourseRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Course createModule(Course module) {
        return moduleRepository.save(module);
    }

    public Course getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with id " + id));
    }

    public List<Course> getAllModules() {
        return moduleRepository.findAll();
    }

    public List<Course> getModulesByTeacherId(Long teacherId) {
        return moduleRepository.findByTeacherId(teacherId);
    }
}
