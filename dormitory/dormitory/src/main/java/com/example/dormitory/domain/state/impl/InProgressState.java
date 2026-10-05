package com.example.dormitory.domain.state.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;

@Component
public class InProgressState implements RepairRequestState {
    @Override 
    public RepairRequestStatus getStatus() {
        return RepairRequestStatus.IN_PROGRESS;
    }

    @Override 
    public Set<RepairRequestStatus> getAllowedNext() {
        return Set.of(
            RepairRequestStatus.COMPLETED,
            RepairRequestStatus.IN_COMPLETED
        );
    }
}
