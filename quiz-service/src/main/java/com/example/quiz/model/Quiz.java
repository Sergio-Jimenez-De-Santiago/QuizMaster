package com.example.quiz.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Time allowed is required")
    private String timeLeft;

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


   // inside your Quiz entity
@ElementCollection
@CollectionTable(name = "quiz_options",
                 joinColumns = @JoinColumn(name = "quiz_id"))
@MapKeyColumn(name = "question_number")
@Column(name = "option_blob")           // stores "A|B|C"
private Map<Integer, String> optionBlob = new HashMap<>();

@NotNull
private long courseId;

// setter that accepts the convenient Map<Integer, List<String>>
public void setOptions(Map<Integer, List<String>> map) {
    Map<Integer, String> blob = new HashMap<>();
    for (var entry : map.entrySet()) {
        // join the 3 choices with a delimiter (use something you know won't appear in text)
        blob.put(entry.getKey(), String.join("|", entry.getValue()));
    }
    this.optionBlob = blob;
}

// getter that returns a Map<Integer, List<String>> for use in DTOs or templates
public Map<Integer, List<String>> getOptions() {
    Map<Integer, List<String>> map = new HashMap<>();
    for (var entry : optionBlob.entrySet()) {
        // split back into a list; need double backslash to escape in Java source
        map.put(entry.getKey(), Arrays.asList(entry.getValue().split("\\|")));
    }
    return map;
}


    


  

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    
    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", timeLeft='" + timeLeft + '\'' +
                ", questions=" + questions +
                ", teacherAnswers=" + teacherAnswers +
                ", courseId=" + courseId +
                ",options= " + getOptions() +
                '}';
    }
}
