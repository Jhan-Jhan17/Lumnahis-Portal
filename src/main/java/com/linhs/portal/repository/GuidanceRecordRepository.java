package com.linhs.portal.repository;

import com.linhs.portal.model.GuidanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GuidanceRecordRepository extends JpaRepository<GuidanceRecord, Long> {
    List<GuidanceRecord> findByStudentLrn(String studentLrn);

    // Fixed: Now safely returns the real JPA Entity model layer
    List<GuidanceRecord> findByStudentLrnAndStatus(String lrn, String status);
}