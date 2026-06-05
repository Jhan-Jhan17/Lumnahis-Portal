package com.linhs.portal.repository;

import com.linhs.portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    // Fixed: Reverted back to the true database entity model type context
    Optional<Student> findByLrn(String lrn);
    List<Student> findBySection(String section);
}