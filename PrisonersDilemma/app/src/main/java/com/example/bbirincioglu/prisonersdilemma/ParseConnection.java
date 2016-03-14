package com.example.bbirincioglu.prisonersdilemma;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class ParseConnection {
    public static final int STATE_NO_BACKGROUND_JOB = -1;
    public static final int STATE_BACKGROUND_JOB_STARTED = 0;
    public static final int STATE_BACKGROUND_JOB_FINISHED = 1;
    private static ParseConnection instance;
    private ArrayList<ParseConnectionObserver> observers;
    private List<Object> objects;
    private int currentState;

    private ParseConnection() {
        setObservers(new ArrayList<ParseConnectionObserver>());
        setObjects(new ArrayList<Object>());
        setCurrentState(STATE_NO_BACKGROUND_JOB);
    }

    public static ParseConnection getInstance() {
        if (instance == null) {
            instance = new ParseConnection();
        }

        return instance;
    }

    public void obtainObjects(String className) {
        setCurrentState(STATE_BACKGROUND_JOB_STARTED);
        ParseQuery query = ParseQuery.getQuery(className);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    setObjects(list);
                } else {
                    System.out.println("Background Job Problem.");
                    e.printStackTrace();
                }

                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    setObjects((List) o);
                } else {
                    System.out.println("Background Job Problem.");
                    throwable.printStackTrace();
                }

                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
            }
        });
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
        notifyObservers();
    }

    private ArrayList<ParseConnectionObserver> getObservers() {
        return observers;
    }

    private void setObservers(ArrayList<ParseConnectionObserver> observers) {
        this.observers = observers;
    }

    public void addObserver(ParseConnectionObserver observer) {
        getObservers().add(observer);
    }

    public void removeObserver(ParseConnectionObserver observer) {
        getObservers().remove(observer);
    }

    public void notifyObservers() {
        ArrayList<ParseConnectionObserver> observers = getObservers();

        for (ParseConnectionObserver observer : observers) {
            observer.update(this);
        }
    }
}