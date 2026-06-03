package com.linhs.portal.repository;

import com.linhs.portal.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByStatus(String status);
    List<BorrowRecord> findByLrnAndStatus(Long lrn, String status);
}