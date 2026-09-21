package com.example.dormitory.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "reporter")
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

    public Reporter(){
        
    }

    public Reporter(User user, Resident resident) {
        this.user = user;
        this.resident = resident;
    }

    public UUID getReperterId() {
        return reporterId;
    }

    public void setReperterId(UUID reperterId) {
        this.reporterId = reperterId;
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
