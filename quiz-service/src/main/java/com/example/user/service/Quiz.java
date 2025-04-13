package com.example.user.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Quiz {
    private String title;
    private static int COUNTER = 1000;
    private int id;
    private Map<Integer, String> questions = new HashMap<>();
    private double timeLeft = 0;
    private Map<Integer, String> teacherAnswers = new HashMap<>();
    private LocalDate duedate;

    public Quiz(String title, Map<Integer, String> questions, double timeLeft, Map<Integer, String> teacherAnswers,LocalDate duedate) {
        this.id = COUNTER++;
        this.title = title;
        this.questions = questions;
        this.timeLeft = timeLeft;
        this.teacherAnswers = teacherAnswers;
        this.duedate = duedate;
    }

    public void deleteQuiz(int questionToDelete) {
        questions.remove(questionToDelete);
    }

    public void updateQuiz(int questionToUpdate, String replacementQuestion) {
        questions.put(questionToUpdate, replacementQuestion);
    }

    public Map<Integer, String> getQuestions() {
        return questions;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public LocalDate getDueDate() {
        return duedate;
    }

    public Map<Integer, String> getTeacherAnswers() {
        return teacherAnswers;
    }

}
