package com.example.enrolment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enrolment.model.Enrolment;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {
    List<Enrolment> findByStudentId(Long studentId);
    List<Enrolment> findByModuleId(Long moduleId);
    Optional<Enrolment> findByStudentIdAndModuleId(Long studentId, Long moduleId);
}
