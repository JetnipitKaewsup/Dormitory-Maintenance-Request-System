package com.example.dormitory.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "technician")
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID technicianId;
    private String specialization;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Technician() {
    }

    // Getter / Setter

    public UUID getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(UUID technicianId) {
        this.technicianId = technicianId;
    }

    public String getSpecialization(){
        return specialization;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
