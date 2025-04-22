package com.example.frontend.dto;

import java.util.Map;

public class GradeRequestDTO {
    private Long studentId;
    private Long quizId;
    private Map<Integer, String> studentAnswers;
    private Map<Integer, String> correctAnswers;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<Integer, String> getStudentAnswers(){
        return studentAnswers;
    }

    public void setStudentAnswers(Map<Integer, String> studentAnswers){
        this.studentAnswers = studentAnswers;
    }

    public Map<Integer, String> getCorrectAnswers(){
        return correctAnswers;
    }

    public void setCorrectAnswers(Map<Integer, String> correctAnswers){
        this.correctAnswers = correctAnswers;
    }
}


