package com.example.dormitory.event;

public interface RepairAssignmentSubject {

    void addObserver(RepairAssignmentObserver observer);

    void removeObserver(RepairAssignmentObserver observer);

    void notifyObservers(RepairAssignmentCreatedEvent event);
}
