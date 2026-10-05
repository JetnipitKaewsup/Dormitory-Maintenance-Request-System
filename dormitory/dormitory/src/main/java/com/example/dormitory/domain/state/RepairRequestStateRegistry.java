package com.example.dormitory.domain.state;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.dormitory.domain.enums.RepairRequestStatus;

@Component 
public class RepairRequestStateRegistry {
    private final Map<RepairRequestStatus,RepairRequestState> byStatus;

    public RepairRequestStateRegistry(List<RepairRequestState> states){
        this.byStatus = states.stream()
            .collect(Collectors.toMap(
                RepairRequestState::getStatus,
                Function.identity()
            ));
    }

    // ค้นหา state จาก enum 
    public RepairRequestState get(RepairRequestStatus status){
        RepairRequestState s = byStatus.get(status);
        if(s == null){
            throw new IllegalArgumentException("ไม่พบ state : " + status);
        }
        return  s; // state object
    }

    // ตรวจสอบว่าเปลี่ยนสถานะได้หรือไม่
    public boolean canTransition(RepairRequestStatus from, RepairRequestStatus to){
        return  get(from).canTransitionTo(to);
    }

    // state ทั้งหมดในระบบ
    public Collection<RepairRequestState> all() {
        return byStatus.values();
    }


}
