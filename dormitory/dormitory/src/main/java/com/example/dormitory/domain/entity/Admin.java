package com.example.dormitory.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID adminId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Admin() {
    }

    public Admin(User user) {
        this.user = user;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public void setAdminId(UUID adminId) {
        this.adminId = adminId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}