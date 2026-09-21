package com.example.dormitory.model;

import java.util.UUID;

import jakarta.persistence.*;

public class Reporter {
    // PK
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID reporterId;
    // FK
    @OneToOne 
    @JoinColumn (name = "userId")
    private User user;
    
    @OneToOne 
    @JoinColumn (name = "residentId")
    private Resident resident;

    public Reporter(User user, Resident resident) {
        this.user = user;
        this.resident = resident;
    }

    public UUID getReperterId() {
        return reperterId;
    }

    public void setReperterId(UUID reperterId) {
        this.reperterId = reperterId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
