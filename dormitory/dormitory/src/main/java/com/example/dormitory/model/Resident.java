package com.example.dormitory.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "resident")
public class Resident {
    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID residentId;

    @ManyToOne
    @JoinColumn(name = "room_no")
    private Room room;

    private String firstName;
    private String lastName;
    private String phoneNo;

    // Constructor
    public Resident(Room room, String firstName, String lastName, String phoneNo) {
        this.room = room;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
    }
        public Resident() {
    }

    public UUID getResidentId() {
        return residentId;
    }

    public void setResidentId(UUID residentId) {
        this.residentId = residentId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

}
