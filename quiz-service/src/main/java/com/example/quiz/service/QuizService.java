package com.example.quiz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizSubmission;
import com.example.quiz.repository.QuizRepository;
import com.example.quiz.repository.QuizSubmissionRepository;

import jakarta.validation.Valid;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;


    @Autowired
    public QuizService(QuizRepository quizRepository, QuizSubmissionRepository quizSubmissionRepository) {
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    public Quiz createQuiz(@Valid Quiz quiz) {
        if (quizRepository.findById(quiz.getId()).isPresent()) {
            throw new IllegalArgumentException("Quiz already exists");
        }
        System.out.println("quiz saved");
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz findById(int Id) {
        return quizRepository.findById(Id);
    }

    public void deleteQuiz(int Id) {
        quizRepository.deleteById((long)Id);
    }

    public QuizSubmission submit(@Valid QuizSubmission quizSubmission) {
        if (quizSubmissionRepository.findByStudentIdAndQuizId(quizSubmission.getStudentId(),quizSubmission.getQuizId()).isPresent()) {
            throw new IllegalArgumentException("Quiz already submitted");
        }
        System.out.println("quiz submission saved");
        return quizSubmissionRepository.save(quizSubmission);
    }

    public Optional<QuizSubmission> findSubmission(Long studentId , Integer quizId){
        return quizSubmissionRepository.findByStudentIdAndQuizId(studentId, quizId);
    }

    public List<QuizSubmission> getAllSubmissionsForSingularQuiz(Integer quizId){
        return quizSubmissionRepository.findAllByQuizId(quizId);
    }

    public List<QuizSubmission> getAllSubmissionsByStudent(Integer studentId){
        return quizSubmissionRepository.findAllByStudentId(studentId);
    }

}
