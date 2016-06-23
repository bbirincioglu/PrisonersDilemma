package com.example.bbirincioglu.prisonersdilemma;

/**
 * The interface implemented by some GUI classes which listens classes implementing ConnectionThread interface.
 */
public interface ConnectionThreadObserver {
    public void update(ConnectionThread connectionThread);
}
