package com.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Enrolment {

    private Long enrolmentId;
    private Long studentId;
    private Long moduleId;

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

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "Enrolment{" +
                "enrolmentId=" + enrolmentId +
                ", studentId=" + studentId +
                ", moduleId=" + moduleId +
                '}';
    }

}
