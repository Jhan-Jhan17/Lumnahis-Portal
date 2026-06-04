package com.linhs.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linhs.portal.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // This tells the database to sort the announcements from newest to oldest
    List<Announcement> findAllByOrderByCreatedAtDesc();
}