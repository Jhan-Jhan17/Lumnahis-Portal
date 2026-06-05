package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LibraryBorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentLrn;
    private String studentName;
    private String bookTitle;
    private String status;
    private LocalDateTime borrowedAt;

    public LibraryBorrowRecord() {
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(LocalDateTime borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    // =========================================================
    // --- BRIDGE METHOD TO MATCH CONTROLLER EXPECTATIONS ---
    // =========================================================

    // Maps the controller's lbr.getBorrowDate() call to the borrowedAt field
    public LocalDateTime getBorrowDate() {
        return this.borrowedAt;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowedAt = borrowDate;
    }
}