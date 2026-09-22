package com.example.dormitory.model;

import com.example.dormitory.model.User;
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
    @JoinColumn(name = "userId", nullable = false, unique = true)
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
