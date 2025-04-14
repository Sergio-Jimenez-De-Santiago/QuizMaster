package com.example.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Time allowed is required")
    private String timeLeft;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @ElementCollection
    @CollectionTable(name = "quiz_questions", joinColumns = @JoinColumn(name = "quiz_id"))
    @MapKeyColumn(name = "question_number")
    @Column(name = "question_text")
    private Map<Integer, String> questions;

    @ElementCollection
    @CollectionTable(name = "quiz_teacher_answers", joinColumns = @JoinColumn(name = "quiz_id"))
    @MapKeyColumn(name = "question_number")
    @Column(name = "answer_text")
    private Map<Integer, String> teacherAnswers;

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
