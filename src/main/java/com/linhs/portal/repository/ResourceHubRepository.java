package com.linhs.portal.repository;
import com.linhs.portal.repository.ResourceHubRepository; // maps interface
import com.linhs.portal.model.ResourceHub;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ResourceHubRepository extends JpaRepository<ResourceHub, Long> {}