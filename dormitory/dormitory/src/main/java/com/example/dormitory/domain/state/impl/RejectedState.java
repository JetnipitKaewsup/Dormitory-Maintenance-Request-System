package com.example.dormitory.domain.state.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;

@Component
public class RejectedState implements RepairRequestState {
    @Override public RepairRequestStatus getStatus() {
        return RepairRequestStatus.REJECTED;
    }
    
    @Override public Set<RepairRequestStatus> getAllowedNext() {
        return Set.of();   // final state
    }
}
