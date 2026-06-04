package com.linhs.portal.repository;

import com.linhs.portal.model.Liability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {
    // FIXED: Changed parameter type from String to Long to match Student's updated ID field
    List<Liability> findByStudentLrn(Long lrn);
}