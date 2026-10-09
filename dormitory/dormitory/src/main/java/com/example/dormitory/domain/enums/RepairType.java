package com.example.dormitory.domain.enums;

public enum RepairType {

    ELECTRICAL("ไฟฟ้า"),
    PLUMBING("ประปา"),
    CARPENTRY("ไม้"),
    OTHER("อื่น ๆ");

    private final String thaiName;

    RepairType(String thaiName) {
        this.thaiName = thaiName;
    }

    public String getThaiName() {
        return thaiName;
    }
}
