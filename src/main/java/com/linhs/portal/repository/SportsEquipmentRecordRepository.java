package com.linhs.portal.repository;

import com.linhs.portal.model.SportsEquipmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SportsEquipmentRecordRepository extends JpaRepository<SportsEquipmentRecord, Long> {
    List<SportsEquipmentRecord> findByStatus(String status);
    List<SportsEquipmentRecord> findByLrnAndStatus(Long lrn, String status);
}