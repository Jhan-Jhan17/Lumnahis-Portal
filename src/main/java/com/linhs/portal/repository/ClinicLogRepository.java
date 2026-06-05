package com.linhs.portal.repository;

import com.linhs.portal.model.ClinicLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClinicLogRepository extends JpaRepository<ClinicLog, Long> {
    List<ClinicLog> findAllByOrderByLoggedAtDesc();
}