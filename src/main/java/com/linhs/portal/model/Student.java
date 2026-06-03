package com.linhs.portal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private Long lrn; // Main Primary Key (Long)
    
    @Column(nullable = false)
    private String name;
    
    private String section;
    
    // --- CLEARANCE FIELDS FOR ALL DASHBOARDS ---
    private String adviserClearance;
    private String sportsClearance;
    private String labClearance;
    private String registrarClearance;
    private String guidanceClearance;
    private String nurseClearance;
    private String facilitiesClearance;

    // Default Constructor
    public Student() {}

    // Parameterized Constructor
    public Student(Long lrn, String name, String section) {
        this.lrn = lrn;
        this.name = name;
        this.section = section;
    }

    // --- GETTERS AND SETTERS ---
    
    public Long getLrn() { 
        return lrn; 
    }
    
    public void setLrn(Long lrn) { 
        this.lrn = lrn; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getSection() { 
        return section; 
    }
    
    public void setSection(String section) { 
        this.section = section; 
    }

    public String getAdviserClearance() { 
        return adviserClearance; 
    }
    
    public void setAdviserClearance(String adviserClearance) { 
        this.adviserClearance = adviserClearance; 
    }

    public String getSportsClearance() { 
        return sportsClearance; 
    }
    
    public void setSportsClearance(String sportsClearance) { 
        this.sportsClearance = sportsClearance; 
    }

    public String getLabClearance() { 
        return labClearance; 
    }
    
    public void setLabClearance(String labClearance) { 
        this.labClearance = labClearance; 
    }

    public String getRegistrarClearance() { 
        return registrarClearance; 
    }
    
    public void setRegistrarClearance(String registrarClearance) { 
        this.registrarClearance = registrarClearance; 
    }

    public String getGuidanceClearance() { 
        return guidanceClearance; 
    }
    
    public void setGuidanceClearance(String guidanceClearance) { 
        this.guidanceClearance = guidanceClearance; 
    }

    public String getNurseClearance() { 
        return nurseClearance; 
    }
    
    public void setNurseClearance(String nurseClearance) { 
        this.nurseClearance = nurseClearance; 
    }

    public String getFacilitiesClearance() { 
        return facilitiesClearance; 
    }
    
    public void setFacilitiesClearance(String facilitiesClearance) { 
        this.facilitiesClearance = facilitiesClearance; 
    }
}