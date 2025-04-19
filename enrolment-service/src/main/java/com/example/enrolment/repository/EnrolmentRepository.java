package com.example.enrolment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enrolment.model.Enrolment;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {
    List<Enrolment> findByStudentId(Long studentId);
    List<Enrolment> findByCourseId(Long courseId);
    Optional<Enrolment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
