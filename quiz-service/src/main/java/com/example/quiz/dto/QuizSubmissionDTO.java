package com.example.quiz.dto;

import java.util.Map;

import com.example.quiz.model.QuizSubmission;

public class QuizSubmissionDTO {
    private Long id;
    private Long studentId;
    private Integer quizId;
    private Map<Integer, String> studentAnswers;

    public QuizSubmissionDTO() {
    }

    public QuizSubmissionDTO(Long id, Long studentId, Integer quizId, Map<Integer, String> studentAnswers) {
        this.id = id;
        this.studentId = studentId;
        this.quizId = quizId;
        this.studentAnswers = studentAnswers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Map<Integer, String> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(Map<Integer, String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    public QuizSubmission toEntity() {
        QuizSubmission entity = new QuizSubmission();
        entity.setId(this.id);
        entity.setStudentId(this.studentId);
        entity.setQuizId(this.quizId);
        entity.setStudentAnswers(this.studentAnswers);
        return entity;
    }


    @Override
    public String toString() {
        return "QuizSubmissionDTO{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", quizId=" + quizId +
                ", studentAnswers=" + studentAnswers +
                '}';
    }
}
