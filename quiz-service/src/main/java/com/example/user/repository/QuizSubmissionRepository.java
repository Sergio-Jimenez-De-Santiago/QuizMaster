package com.example.user.repository;

import com.example.user.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    Optional<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Integer quizId);

    List<QuizSubmission> findAllByQuizId(Integer quizId);

    List<QuizSubmission> findAllByStudentId(Integer studentId);
}
