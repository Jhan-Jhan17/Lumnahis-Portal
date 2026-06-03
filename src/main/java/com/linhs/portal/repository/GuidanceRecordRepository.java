package com.linhs.portal.repository;

import com.linhs.portal.model.GuidanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuidanceRecordRepository extends JpaRepository<GuidanceRecord, Long> {
    List<GuidanceRecord> findByIsResolvedFalse();
    List<GuidanceRecord> findByStudentLrnAndIsResolvedFalse(Long studentLrn);
}