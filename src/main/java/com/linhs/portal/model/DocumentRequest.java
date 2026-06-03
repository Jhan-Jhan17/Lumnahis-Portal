package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
public class DocumentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long studentLrn;
    private String studentName;
    private String documentType;
    private LocalDateTime requestedAt;
    private boolean isCompleted;

    public DocumentRequest() {}

    public DocumentRequest(Long studentLrn, String studentName, String documentType, LocalDateTime requestedAt, boolean isCompleted) {
        this.studentLrn = studentLrn;
        this.studentName = studentName;
        this.documentType = documentType;
        this.requestedAt = requestedAt;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
}