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
    @JoinColumn (name = "user_id")
    private User user;
    
    @OneToOne 
    @JoinColumn (name = "resident_id")
    private Resident resident;

    public Reporter(){

    }

    public Reporter(User user, Resident resident) {
        this.user = user;
        this.resident = resident;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reperterId) {
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
