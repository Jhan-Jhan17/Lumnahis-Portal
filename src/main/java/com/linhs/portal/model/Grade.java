package com.linhs.portal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_lrn", referencedColumnName = "lrn", nullable = false)
    private Student student;

    // The 8 Subjects
    private Integer mathGrade;
    private Integer scienceGrade;
    private Integer englishGrade;
    private Integer filipinoGrade;
    private Integer historyGrade;
    private Integer peGrade;
    private Integer tleGrade;
    private Integer valuesGrade;
    private Double genAvg;

    public Grade() {}

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Integer getMathGrade() { return mathGrade; }
    public void setMathGrade(Integer mathGrade) { this.mathGrade = mathGrade; }
    public Integer getScienceGrade() { return scienceGrade; }
    public void setScienceGrade(Integer scienceGrade) { this.scienceGrade = scienceGrade; }
    public Integer getEnglishGrade() { return englishGrade; }
    public void setEnglishGrade(Integer englishGrade) { this.englishGrade = englishGrade; }
    public Integer getFilipinoGrade() { return filipinoGrade; }
    public void setFilipinoGrade(Integer filipinoGrade) { this.filipinoGrade = filipinoGrade; }
    public Integer getHistoryGrade() { return historyGrade; }
    public void setHistoryGrade(Integer historyGrade) { this.historyGrade = historyGrade; }
    public Integer getPeGrade() { return peGrade; }
    public void setPeGrade(Integer peGrade) { this.peGrade = peGrade; }
    public Integer getTleGrade() { return tleGrade; }
    public void setTleGrade(Integer tleGrade) { this.tleGrade = tleGrade; }
    public Integer getValuesGrade() { return valuesGrade; }
    public void setValuesGrade(Integer valuesGrade) { this.valuesGrade = valuesGrade; }
    public Double getGenAvg() { return genAvg; }
    public void setGenAvg(Double genAvg) { this.genAvg = genAvg; }
}