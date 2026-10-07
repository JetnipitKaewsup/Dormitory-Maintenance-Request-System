package com.example.dormitory.event;

public interface RepairAssignmentObserver {

    void onAssignmentCreated(
            RepairAssignmentCreatedEvent event
    );
}
