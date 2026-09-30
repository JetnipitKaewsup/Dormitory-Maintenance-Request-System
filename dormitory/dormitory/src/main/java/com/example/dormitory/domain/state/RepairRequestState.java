package com.example.dormitory.domain.state;

import java.util.Set;

import com.example.dormitory.domain.enums.RepairRequestStatus;

public interface RepairRequestState {
    RepairRequestStatus getStatus();

    Set<RepairRequestStatus> getAllowedNext();

    // ตรวจสอบว่าสามารถเปลี่ยนสถานะคำร้องได้หรือไม่
    default boolean canTransitionTo(RepairRequestStatus next){
        return next != null && getAllowedNext().contains(next);
    }

    default boolean isFinal(){
        return getAllowedNext().isEmpty();
    }

}
