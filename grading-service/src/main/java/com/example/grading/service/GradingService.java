package com.example.grading.service;

import com.example.grading.model.Grade;
import com.example.grading.repository.GradeRepository;
import com.example.grading.dto.GradeRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradingService {

    @Autowired
    private GradeRepository gradeRepository;

    public Grade calculateAndSaveGrade(GradeRequestDTO dto) {
        Map<Integer, String> studentAnswers = dto.getStudentAnswers();
        Map<Integer, String> correctAnswers = dto.getCorrectAnswers();

        int totalQuestions = correctAnswers.size();
        int correctCount = 0;

        for (Map.Entry<Integer, String> entry : correctAnswers.entrySet()) {
            Integer questionId = entry.getKey();
            String correctAnswer = entry.getValue();

            String studentAnswer = studentAnswers.get(questionId);
            if (studentAnswer != null && studentAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount++;
            }
        }

        int score = Math.round((float) correctCount / totalQuestions * 100);

        Grade grade = new Grade();
        grade.setQuizId(dto.getQuizId());
        grade.setStudentId(dto.getStudentId());
        grade.setScore(score);

        return gradeRepository.save(grade);
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }
    
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id).orElse(null);
    }
    
    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public Optional<Grade> getGradesByStudentIdAndQuizId(Long studentId, Long quizId) {
        return gradeRepository.findByStudentIdAndQuizId(studentId, quizId);
    }
    
}
