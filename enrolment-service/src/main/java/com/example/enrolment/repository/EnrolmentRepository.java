package com.example.enrolment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {
    List<Enrolment> findByStudentId(Long studentId);
    List<Enrolment> findByModuleId(Long moduleId);
    Optional<Enrolment> findByStudentIdAndModuleId(Long studentId, Long moduleId);
}
