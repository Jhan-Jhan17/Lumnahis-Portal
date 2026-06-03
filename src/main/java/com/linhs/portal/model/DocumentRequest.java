package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "document_requests")
public class DocumentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIXED: Changed type from String to Long
    @Column(nullable = false)
    private Long studentLrn;

    @Column(nullable = false)
    private String documentName; // "Form 137", "Good Moral", etc.

    @Column(nullable = false)
    private String status = "PENDING"; // "PENDING" or "RELEASED"

    private LocalDate requestDate;

    public DocumentRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // FIXED: Getter and Setter type signatures
    public Long getStudentLrn() { return studentLrn; }
    public void setStudentLrn(Long studentLrn) { this.studentLrn = studentLrn; }
    
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
}