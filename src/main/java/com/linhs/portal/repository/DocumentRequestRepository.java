package com.linhs.portal.repository;

import com.linhs.portal.model.DocumentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
}