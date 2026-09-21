package com.example.dormitory.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Entity 
@Table (name = "TenantRegistry")
public class TenantRegistry {
    
    // PK
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID tenantRegistryId;

    // FK
    @ManyToOne 
    @JoinColumn (name = "residentId")
    private Resident resident;

    @ManyToOne 
    @JoinColumn (name = "roomNo")
    private Room room;

    private LocalDate startDate;
    private LocalDate endDate;
}
