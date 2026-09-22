package com.example.dormitory.model;

import jakarta.persistence.*;

@Entity 
@Table (name = "Building")
public class Building {
    // PK
    @Id 
    private int buildingNo;

    // FK
    /* 
    @ManyToOne
    @JoinColumn (name = "dormitoryId")
    private Dormitory dormitory;
*/
    private String buildingName;
    private int totalFloor;

    public Building() {
    }

    public int getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(int buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public void setTotalFloor(int totalFloor) {
        this.totalFloor = totalFloor;
    }

    public Building(int buildingNo, String buildingName, int totalFloor) {
        this.buildingNo = buildingNo;
        this.buildingName = buildingName;
        this.totalFloor = totalFloor;
    }
    
}
