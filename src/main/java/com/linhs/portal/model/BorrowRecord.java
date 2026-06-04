package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lrn;
    private String studentName;
    private String equipmentName;
    private LocalDateTime borrowDate;
    private String status; // "BORROWED" or "RETURNED"

    // Constructors
    public BorrowRecord() {}

    public BorrowRecord(Long lrn, String studentName, String equipmentName, LocalDateTime borrowDate, String status) {
        this.lrn = lrn;
        this.studentName = studentName;
        this.equipmentName = equipmentName;
        this.borrowDate = borrowDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLrn() { return lrn; }
    public void setLrn(Long lrn) { this.lrn = lrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public LocalDateTime getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDateTime borrowDate) { this.borrowDate = borrowDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}