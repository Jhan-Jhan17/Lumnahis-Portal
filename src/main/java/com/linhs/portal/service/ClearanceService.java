package com.linhs.portal.service;

import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.GuidanceRecord;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.GuidanceRecordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClearanceService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final GuidanceRecordRepository guidanceRecordRepository;

    public ClearanceService(BorrowRecordRepository borrowRecordRepository, 
                            GuidanceRecordRepository guidanceRecordRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.guidanceRecordRepository = guidanceRecordRepository;
    }

    /**
     * Aggregates active student financial, material, or behavioral liabilities.
     * Maps across the unified student tracking schemas.
     */
    public List<String> calculateLiabilities(String lrn) {
        List<String> liabilitiesList = new ArrayList<>();

        // 1. Check General Asset & Lab Borrowing Liabilities
        List<BorrowRecord> unreturnedAssets = borrowRecordRepository.findByStudentLrn(lrn);
        for (BorrowRecord record : unreturnedAssets) {
            if ("BORROWED".equalsIgnoreCase(record.getStatus())) {
                liabilitiesList.add(String.format("Unreturned item: '%s' (Borrowed: %s)", 
                    record.getItemName(), record.getBorrowedAt()));
            }
        }

        // 2. Check Guidance Behavioral/Incident Cases
        List<GuidanceRecord> infractions = guidanceRecordRepository.findByStudentLrn(lrn);
        for (GuidanceRecord incident : infractions) {
            if (incident.getActionTaken() == null || incident.getActionTaken().isBlank()) {
                liabilitiesList.add(String.format("Unresolved case record inside Guidance Office: %s (Logged: %s)", 
                    incident.getIncidentDetails(), incident.getCreatedAt()));
            }
        }

        return liabilitiesList;
    }
}