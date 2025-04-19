package com.example.frontend.model;

public class Enrolment {

    private Long enrolmentId;
    private Long studentId;
    private Long courseId;

    // Getters and setters

    public Long getEnrolmentId() {
        return enrolmentId;
    }

    public void setEnrolmentId(Long enrolmentId) {
        this.enrolmentId = enrolmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "Enrolment{" +
                "enrolmentId=" + enrolmentId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                '}';
    }

}
