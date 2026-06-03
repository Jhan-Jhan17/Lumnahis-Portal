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

    public List<String> calculateLiabilities(Long lrn) {
        List<String> liabilitiesList = new ArrayList<>();

        // 1. Check Science Lab Equipment Liabilities
        List<BorrowRecord> unreturnedAssets = borrowRecordRepository.findByLrnAndStatus(lrn, "BORROWED");
        for (BorrowRecord record : unreturnedAssets) {
            liabilitiesList.add(String.format("Unreturned item: '%s' from Science Laboratory (Borrowed: %s)", 
                record.getEquipmentName(), record.getBorrowDate()));
        }

        // 2. Check Guidance Behavior Violations
        List<GuidanceRecord> activeInfractions = guidanceRecordRepository.findByStudentLrnAndIsResolvedFalse(lrn);
        for (GuidanceRecord incident : activeInfractions) {
            liabilitiesList.add(String.format("Unresolved case record inside Guidance Office: %s (Logged: %s)", 
                incident.getInfractionType(), incident.getIncidentDate()));
        }

        return liabilitiesList;
    }
}