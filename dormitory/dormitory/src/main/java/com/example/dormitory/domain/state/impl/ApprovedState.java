package com.example.dormitory.domain.state.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;

@Component
public class ApprovedState implements RepairRequestState {
    @Override 
    public RepairRequestStatus getStatus() {
        return RepairRequestStatus.APPROVED;
    }
    @Override 
    public Set<RepairRequestStatus> getAllowedNext() {
        return Set.of(
            RepairRequestStatus.IN_PROGRESS,
            RepairRequestStatus.CANCELLED
        );
    }
}
