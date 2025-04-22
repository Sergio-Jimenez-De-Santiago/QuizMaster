
package com.example.frontend.model;

public class Grade {
    private Long gradeId;
    private int score;
    private Long studentId;
    private Long quizId;

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
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

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", score=" + score +
                ", studentId=" + studentId +
                ", quizId=" + quizId +
                '}';
    }
}