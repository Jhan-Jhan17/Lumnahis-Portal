package com.linhs.portal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ✅ FIX: Aligned with standard lower-case naming strategy to avoid Postgres column crashes.
    // If your physical PostgreSQL table column has no underscore, use "assignedsection".
    @Column(name = "assignedsection", nullable = true)
    private String assignedSection;

    // Constructors
    public User() {}

    public User(String email, String password, String name, Role role, String assignedSection) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.assignedSection = assignedSection;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getAssignedSection() { return assignedSection; }
    public void setAssignedSection(String assignedSection) { this.assignedSection = assignedSection; }
}