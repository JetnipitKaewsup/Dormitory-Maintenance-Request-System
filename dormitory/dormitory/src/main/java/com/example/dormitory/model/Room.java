package com.example.dormitory.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Room")
public class Room {
    // PK
    @Id
    private int roomNo;

    // FK
    @ManyToOne
    @JoinColumn(name = "buildingNo")
    private Building building;

    @OneToMany(mappedBy = "room")
    private List<Resident> residents = new ArrayList<>();

}
