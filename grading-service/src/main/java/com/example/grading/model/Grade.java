package com.example.grading.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gradeId;

    @NotNull(message = "Score is mandatory")
    private int score;

    @NotNull(message = "StudentId is mandatory")
    private Long studentId;

    @NotNull(message = "QuizId is mandatory")
    private Long quizId;

    public Grade() {
    }

    public Grade(Long studentId, Long quizId, int score) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.score = score;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", score=" + score +
                ", studentId=" + studentId +
                ", quizId=" + quizId +
                '}';
    }
}