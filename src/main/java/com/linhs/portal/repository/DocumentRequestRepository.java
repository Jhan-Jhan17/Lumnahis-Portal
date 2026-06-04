package com.linhs.portal.repository;
import com.linhs.portal.model.DocumentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    List<DocumentRequest> findByIsCompletedFalse();
}