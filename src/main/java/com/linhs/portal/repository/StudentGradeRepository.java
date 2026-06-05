package com.linhs.portal.repository;

import com.linhs.portal.model.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudentLrn(String studentLrn);
    List<StudentGrade> findByStudentLrnIn(List<String> studentLrns);
}