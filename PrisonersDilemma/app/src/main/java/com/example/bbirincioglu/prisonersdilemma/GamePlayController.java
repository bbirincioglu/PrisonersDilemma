package com.example.bbirincioglu.prisonersdilemma;

/**
 * Created by bbirincioglu on 3/14/2016.
 */
public class GamePlayController {
    private static GamePlayController instance;
    private ConnectedThread connectedThread;

    private GamePlayController() {

    }

    public static GamePlayController getInstance() {
        if (instance == null) {
            instance = new GamePlayController();
        }

        return instance;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public void doWrite(String message) {
        getConnectedThread().write(message);
    }
}
