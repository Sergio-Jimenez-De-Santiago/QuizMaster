package com.example.quiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quiz.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findById(int Id);
    List<Quiz> findByCourseId(Long courseId);
    Optional<Quiz> findByTitleAndCourseId(String title, Long courseId);
}
