package com.linhs.portal.repository;

import com.linhs.portal.model.SportsEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SportsEquipmentRepository extends JpaRepository<SportsEquipment, Long> {
    List<SportsEquipment> findByStudentLrnAndStatus(String studentLrn, String status);
}