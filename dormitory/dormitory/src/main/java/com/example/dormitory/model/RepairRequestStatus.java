package com.example.dormitory.model;

public enum RepairRequestStatus {
    SUBMITTED("รอดำเนินการ", "submitted"),
    APPROVED("อนุมัติ", "approved"),
    REJECTED("ไม่อนุมัติ", "rejected"),
    IN_PROGRESS("กำลังดำเนินการ", "progress"),
    COMPLETED("ดำเนินการเสร็จสิ้น", "complete"),
    CANCELLED("ยกเลิก", "cancelled");

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
