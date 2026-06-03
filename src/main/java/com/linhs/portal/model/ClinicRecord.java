package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinic_records")
public class ClinicRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIXED: Changed type from String to Long
    @Column(nullable = false)
    private Long studentLrn;

    @Column(nullable = false)
    private String complaints;

    private LocalDateTime admissionTime;

    public ClinicRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // FIXED: Getter and Setter type signatures
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    
    public String getComplaints() { return complaints; }
    public void setComplaints(String complaints) { this.complaints = complaints; }
    public LocalDateTime getAdmissionTime() { return admissionTime; }
    public void setAdmissionTime(LocalDateTime admissionTime) { this.admissionTime = admissionTime; }
}