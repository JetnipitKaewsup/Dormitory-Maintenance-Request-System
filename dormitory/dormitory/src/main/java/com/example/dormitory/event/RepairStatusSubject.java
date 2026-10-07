package com.example.dormitory.event;

public interface RepairStatusSubject {

    void addObserver(
            RepairStatusObserver observer
    );

    void removeObserver(
            RepairStatusObserver observer
    );

    void notifyObservers(
            RepairStatusChangedEvent event
    );
}
