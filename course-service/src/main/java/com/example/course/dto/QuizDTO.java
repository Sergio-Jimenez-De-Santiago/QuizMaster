package com.example.course.dto;

public class QuizDTO {

    private int id;
    private String title;
    private String timeLeft;
    private long courseId;

    // private Map<Integer, String> questions; include only if needed
    // private Map<Integer, String> teacherAnswers; include only if needed

    public QuizDTO() {}

    public QuizDTO(int id, String title, String timeLeft, long courseId) {
        this.id = id;
        this.title = title;
        this.timeLeft = timeLeft;
        this.courseId = courseId;
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

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "QuizDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", timeLeft='" + timeLeft + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}
