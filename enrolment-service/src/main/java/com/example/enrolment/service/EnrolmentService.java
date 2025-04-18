package com.example.enrolment.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Service
public class EnrolmentService {
    private final EnrolmentRepository enrolmentRepository;

    public EnrolmentService(EnrolmentRepository enrolmentRepository) {
        this.enrolmentRepository = enrolmentRepository;
    }

    public Enrolment createEnrolment(@Valid Enrolment enrolment) {
        Optional<Enrolment> existing = enrolmentRepository
                .findByStudentIdAndModuleId(enrolment.getStudentId(), enrolment.getModuleId());

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Student already enrolled in this module");
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

    public List<Enrolment> getEnrolmentsByModuleId(Long moduleId) {
        return enrolmentRepository.findByModuleId(moduleId);
    }
}

