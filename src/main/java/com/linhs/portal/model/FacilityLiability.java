package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "facility_liabilities")
public class FacilityLiability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long studentLrn;
    private String studentName;
    private String section;
    private String itemDescription;
    private LocalDateTime loggedAt;
    private boolean isResolved;

    public FacilityLiability() {}

    public FacilityLiability(Long studentLrn, String studentName, String section, String itemDescription, LocalDateTime loggedAt, boolean isResolved) {
        this.studentLrn = studentLrn;
        this.studentName = studentName;
        this.section = section;
        this.itemDescription = itemDescription;
        this.loggedAt = loggedAt;
        this.isResolved = isResolved;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
    public boolean getIsResolved() { return isResolved; }
    public void setIsResolved(boolean isResolved) { this.isResolved = isResolved; }
}