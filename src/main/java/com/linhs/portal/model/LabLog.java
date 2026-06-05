package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_logs")
public class LabLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_lrn", nullable = false)
    private String studentLrn;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "equipment_details", nullable = false, columnDefinition = "TEXT")
    private String equipmentDetails;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    public LabLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentLrn() {
        return studentLrn;
    }

    public void setStudentLrn(String studentLrn) {
        this.studentLrn = studentLrn;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEquipmentDetails() {
        return equipmentDetails;
    }

    public void setEquipmentDetails(String equipmentDetails) {
        this.equipmentDetails = equipmentDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}