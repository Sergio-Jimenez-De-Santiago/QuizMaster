package com.example.grading.repository;

import com.example.grading.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;


@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByQuizId(Long quizId);
    List<Grade> findByStudentId(Long studentId);
    Optional<Grade> findByStudentIdAndQuizId(Long studentId, Long quizId);
}
