package com.example.quiz.service;

import java.util.HashMap;
import java.util.Map;

public class QuizSession {

    private String studentId;
    private int quizId;
    private long startTime;
    private boolean submitted;
    private Map<Integer, String> studentAnswers = new HashMap<>();

    public QuizSession(String studentId, int quizId) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.startTime = System.currentTimeMillis();
        this.submitted = false;
    }

public String getStudentID(){
    return studentId;
}

public int getQuizId(){
    return quizId;
}

public long getStartTime(){
    return startTime;
}

public boolean isSubmitted(){
    return submitted;
}

public void setSubmitted(){
    submitted = true;
}

public void setStudentAnswers(Map<Integer, String> studentAnswers){
    this.studentAnswers = studentAnswers;
}

public Map<Integer,String> getStudentAnswers(){
    return studentAnswers;
}
}
