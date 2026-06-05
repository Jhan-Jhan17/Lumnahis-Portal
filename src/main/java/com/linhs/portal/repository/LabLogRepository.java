package com.linhs.portal.repository;

import com.linhs.portal.model.LabLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabLogRepository extends JpaRepository<LabLog, Long> {
    List<LabLog> findByStatus(String status);
}
