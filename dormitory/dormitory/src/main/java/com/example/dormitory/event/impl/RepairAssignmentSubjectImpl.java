package com.example.dormitory.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairAssignmentObserver;
import com.example.dormitory.event.RepairAssignmentSubject;

@Component
public class RepairAssignmentSubjectImpl
        implements RepairAssignmentSubject {

    private final List<RepairAssignmentObserver> observers;

    public RepairAssignmentSubjectImpl(List<RepairAssignmentObserver> observers) {
        this.observers = new ArrayList<>(observers);
    }

    @Override
    public void addObserver(RepairAssignmentObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(RepairAssignmentObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(RepairAssignmentCreatedEvent event) {
        //เทส
    System.out.println(
            ">>> ASSIGNMENT SUBJECT: notifying observers"
            + " | count=" + observers.size()
    );
        //เทส
        for (RepairAssignmentObserver observer : observers) {
            //เทส
                    System.out.println(
                ">>> ASSIGNMENT SUBJECT: observer = "
                + observer.getClass().getSimpleName()
        );
            //เทส
            observer.onAssignmentCreated(event);
        }
    }
}
