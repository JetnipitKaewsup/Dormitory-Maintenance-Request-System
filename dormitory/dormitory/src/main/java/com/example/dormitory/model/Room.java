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
    
     // Constructor เปล่า 
    public Room() {
    }

    public Room(int roomNo, Building building) {
        this.roomNo = roomNo;
        this.building = building;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

}
