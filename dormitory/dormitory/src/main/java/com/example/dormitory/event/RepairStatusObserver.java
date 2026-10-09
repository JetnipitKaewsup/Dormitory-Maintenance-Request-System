package com.example.dormitory.event;

public interface RepairStatusObserver {

    void onStatusChanged(
            RepairStatusChangedEvent event
    );
}
