package com.example.frontend.model;

import java.time.LocalDate;
import java.util.Map;

public class Quiz {
    private long id;

    private String title;
    private double timeLeft;
    private LocalDate dueDate;
    private Map<Integer, String> questions;
    private Map<Integer, String> teacherAnswers;

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

    public double getTimeLeft() {
        return timeLeft;
    }
    public void setTimeLeft(double timeLeft) {
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
