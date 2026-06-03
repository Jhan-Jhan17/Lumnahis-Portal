package com.linhs.portal.repository;

import com.linhs.portal.model.ClinicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClinicRecordRepository extends JpaRepository<ClinicRecord, Long> {
    List<ClinicRecord> findByStudentLrn(Long lrn);
}