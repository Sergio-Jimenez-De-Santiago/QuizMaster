package com.example.frontend.model;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Quiz {
    private long id;

    private String title;
    private String timeLeft;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Map<Integer, String> questions;
    private Map<Integer, String> teacherAnswers;
    @JsonIgnore
    private transient String questionsText;
    @JsonIgnore
    private transient String teacherAnswersText;

    // Getters and setters
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", timeLeft=" + timeLeft +
                ", dueDate=" + dueDate +
                ", questions=" + questions +
                ", teacherAnswers=" + teacherAnswers +
                '}';
    }

}
