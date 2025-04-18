package com.example.enrolment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "enrolments")
public class Enrolment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long enrolmentId;

    @NotNull(message = "StudentId is mandatory")
    private Long studentId;

    @NotNull(message = "ModuleId is mandatory")
    private Long moduleId;

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
