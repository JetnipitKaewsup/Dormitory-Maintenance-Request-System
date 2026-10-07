package com.example.dormitory.event.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.dormitory.event.RepairStatusChangedEvent;
import com.example.dormitory.event.RepairStatusObserver;
import com.example.dormitory.event.RepairStatusSubject;

import org.springframework.stereotype.Component;

@Component
public class RepairStatusSubjectImpl
        implements RepairStatusSubject {

    private final List<RepairStatusObserver> observers;

    public RepairStatusSubjectImpl(
            List<RepairStatusObserver> observers) {

        this.observers =
                new ArrayList<>(observers);
    }

    @Override
    public void addObserver(
            RepairStatusObserver observer) {

        observers.add(observer);
    }

    @Override
    public void removeObserver(
            RepairStatusObserver observer) {

        observers.remove(observer);
    }

    @Override
    public void notifyObservers(RepairStatusChangedEvent event) {

            //เทส
    System.out.println(
            ">>> SUBJECT: notifying observers"
            + " | count=" + observers.size()
    );
            //เทส
        for (RepairStatusObserver observer
                : observers) {

            observer.onStatusChanged(event);
        }
    }
}