package com.example.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user.model.Quiz;
import com.example.user.repository.QuizRepository;

import jakarta.validation.Valid;

@Service
public class QuizService {
    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
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
}
