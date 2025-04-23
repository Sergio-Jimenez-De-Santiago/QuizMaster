package com.example.enrolment.service;

import com.example.enrolment.repository.EnrolmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;
import com.example.enrolment.model.Enrolment;
import java.util.Optional;

@Service
public class EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;

    @Autowired
    public EnrolmentService(EnrolmentRepository enrolmentRepository) {
        this.enrolmentRepository = enrolmentRepository;
    }

    public Enrolment createEnrolment(@Valid Enrolment enrolment) {
        Optional<Enrolment> existing = enrolmentRepository
                .findByStudentIdAndCourseId(enrolment.getStudentId(), enrolment.getCourseId());

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Student already enrolled in this course");
        }

        return enrolmentRepository.save(enrolment);
    }

    public List<Enrolment> getAllEnrolments() {
        return enrolmentRepository.findAll();
    }

    public Enrolment getEnrolmentById(Long id) {
        return enrolmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrolment not found with id " + id));
    }

    public List<Enrolment> getEnrolmentsByStudentId(Long studentId) {
        return enrolmentRepository.findByStudentId(studentId);
    }

    public List<Enrolment> getEnrolmentsByCourseId(Long courseId) {
        return enrolmentRepository.findByCourseId(courseId);
    }

    public List<Enrolment> getEnrolmentsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return enrolmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .map(List::of)
                .orElse(List.of());
    }

    public void deleteEnrolment(long Id) {
        enrolmentRepository.deleteById(Id);
    }
}