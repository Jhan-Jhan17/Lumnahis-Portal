package com.linhs.portal.repository;

import com.linhs.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    // ADD THIS LINE FIXED:
    boolean existsByEmail(String email);
}