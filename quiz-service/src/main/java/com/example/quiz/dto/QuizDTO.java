package com.example.quiz.dto;

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

    private long courseId;

    // No-args constructor
    public QuizDTO() {
    }

    // All-args constructor (optional)
    public QuizDTO(long id, String title, String timeLeft,
            Map<Integer, String> questions,
            Map<Integer, String> teacherAnswers,
            Map<Integer, String> studentAnswers,
            Map<Integer, List<String>> options,
            long courseId) {
        this.id = id;
        this.title = title;
        this.timeLeft = timeLeft;
        this.questions = questions;
        this.teacherAnswers = teacherAnswers;
        this.studentAnswers = studentAnswers;
        this.courseId = courseId;
        this.options = options;

    }

    // Getters and Setters
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

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public Map<Integer, List<String>> getOptions() {
        return options;
    }

    public void setOptions(Map<Integer, List<String>> options) {
        this.options = options;
    }

    @Override public String toString() {
        return "QuizDTO{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", timeLeft='" + timeLeft + '\'' +
               ", questions=" + questions +
               ", teacherAnswers=" + teacherAnswers +
               ", studentAnswers=" + studentAnswers +
               ", options=" + options +
               ", courseId=" + courseId +
               '}';
    }
}
