package com.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz {
    private long id;
    private String title;
    private String timeLeft;
    private Map<Integer, String> questions;
    private Map<Integer, String> teacherAnswers;
    @JsonIgnore
    private transient String questionsText;
    @JsonIgnore
    private transient String teacherAnswersText;
    private long courseId;

    private Map<Integer, List<String>> options = new HashMap<>();

    public String getQuestionsText() {
        return questionsText;
    }

    public void setQuestionsText(String questionsText) {
        this.questionsText = questionsText;
    }

    public String getTeacherAnswersText() {
        return teacherAnswersText;
    }

    public void setTeacherAnswersText(String teacherAnswersText) {
        this.teacherAnswersText = teacherAnswersText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
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

    public Map<Integer, List<String>> getOptions(){
        return options;
    }

    public void setOptions(Map<Integer, List<String>> options){
        this.options = options;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", timeLeft=" + timeLeft +
                ", questions=" + questions +
                ", teacherAnswers=" + teacherAnswers +
                ", courseId=" + courseId +
                ", options= " + options + 
                '}';
    }

}
