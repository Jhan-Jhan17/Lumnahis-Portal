package com.linhs.portal.repository;

import com.linhs.portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// FIXED: Second generic parameter matches the explicit type profile of Student ID (@Id Long)
public interface StudentRepository extends JpaRepository<Student, Long> {
}