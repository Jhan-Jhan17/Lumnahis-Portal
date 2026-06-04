package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "guidance_records")
public class GuidanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentLrn;
    private String studentName;
    private String infractionType; // "Tardiness", "Uniform Violation", "Counseling Session", etc.
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    private LocalDateTime incidentDate;
    private boolean isResolved;

    public GuidanceRecord() {}

    public GuidanceRecord(Long studentLrn, String studentName, String infractionType, String details, LocalDateTime incidentDate, boolean isResolved) {
        this.studentLrn = studentLrn;
        this.studentName = studentName;
        this.infractionType = infractionType;
        this.details = details;
        this.incidentDate = incidentDate;
        this.isResolved = isResolved;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getInfractionType() { return infractionType; }
    public void setInfractionType(String infractionType) { this.infractionType = infractionType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }
    public boolean getIsResolved() { return isResolved; }
    public void setIsResolved(boolean isResolved) { this.isResolved = isResolved; }
}