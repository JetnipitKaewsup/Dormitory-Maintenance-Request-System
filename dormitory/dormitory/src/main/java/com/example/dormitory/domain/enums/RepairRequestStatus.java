package com.example.dormitory.domain.enums;

public enum RepairRequestStatus {
    PENDING("รอดำเนินการ", "pending"),
    APPROVED("อนุมัติ", "approved"),
    REJECTED("ไม่อนุมัติ", "rejected"),
    IN_PROGRESS("กำลังดำเนินการ", "progress"),
    COMPLETED("ดำเนินการเสร็จสิ้น", "complete"),
    IN_COMPLETED("ดำเนินการไม่สำเร็จ", "in_complete");

    private final String thaiName;
    private final String cssClass;

    RepairRequestStatus(String thaiName, String cssClass) {
        this.thaiName = thaiName;
        this.cssClass = cssClass;
    }

    public String getThaiName() {
        return thaiName;
    }

    public String getCssClass() {
        return cssClass;
    }
}
