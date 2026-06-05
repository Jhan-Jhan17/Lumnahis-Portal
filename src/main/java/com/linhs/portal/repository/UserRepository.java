package com.linhs.portal.repository;

import com.linhs.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleName(String roleName);

    // --- BRIDGE METHOD TO FIX THE CONTROLLER RED LINES ---
    // Matches the userRepository.findByRole("ADVISER") call in the controller
    default List<User> findByRole(String role) {
        return findByRoleName(role);
    }
}