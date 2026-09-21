package com.example.dormitory.model;

/**
 * Resident
 */
@Entity
@Table(name = "Resident")
public class Resident {
 // PK
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID residentId;

    // FK
    /*
     * @ManyToOne
     * 
     * @JoinColumn(name = "dormitoryId")
     * private Dormitory dormitory;
     */

    @ManyToOne
    @JoinColumn(name = "roomNo")
    private Room room;

    private String firstName;
    private String lastName;
    private String phoneNo;
    
}
