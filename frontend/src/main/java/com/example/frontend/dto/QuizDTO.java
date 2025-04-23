package com.example.frontend.dto;

import java.util.List;
import java.util.Map;

public class QuizDTO {
    private long id;
    private String title;
    private String timeLeft;
    private Map<Integer, String> questions;
    private Map<Integer, String> teacherAnswers;
    private Map<Integer, String> studentAnswers;
    private Map<Integer, List<String>> options;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Map<Integer, String> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, String> questions) {
        this.questions = questions;
    }

    public Map<Integer, String> getTeacherAnswers() {
        return teacherAnswers;
    }

    public void setTeacherAnswers(Map<Integer, String> teacherAnswers) {
        this.teacherAnswers = teacherAnswers;
    }
    public Map<Integer, String> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(Map<Integer, String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    
    public Map<Integer, List<String>> getOptions(){
        return options;
    }

    public void setOptions(Map<Integer, List<String>> options){
        this.options = options;
    }
}
