package com.linhs.portal.model;

import jakarta.persistence.*;

@Entity
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String aboutText;

    public About() {}
    public About(String aboutText) { this.aboutText = aboutText; }

    public Long getId() { return id; }
    public String getAboutText() { return aboutText; }
    public void setAboutText(String aboutText) { this.aboutText = aboutText; }
}