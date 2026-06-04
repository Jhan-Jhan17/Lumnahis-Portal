package com.linhs.portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linhs.portal.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}