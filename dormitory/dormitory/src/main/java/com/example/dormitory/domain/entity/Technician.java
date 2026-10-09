package com.example.dormitory.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;

import com.example.dormitory.domain.entity.User;

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

    public Technician(UUID technicianId, User user, String specialization) {
        this.technicianId = technicianId;
        this.user = user;
        this.specialization = specialization;
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
