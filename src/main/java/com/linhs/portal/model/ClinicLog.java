package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinic_logs")
public class ClinicLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long studentLrn;
    private String studentName;
    private String reason;
    private LocalDateTime loggedAt;

    public ClinicLog() {}

    public ClinicLog(Long studentLrn, String studentName, String reason, LocalDateTime loggedAt) {
        this.studentLrn = studentLrn;
        this.studentName = studentName;
        this.reason = reason;
        this.loggedAt = loggedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}