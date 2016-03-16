package com.example.bbirincioglu.prisonersdilemma;

import android.app.Dialog;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by bbirincioglu on 3/10/2016.
 */
public class GameResultsController {
    private static GameResultsController instance;

    private GameResultsController() {

    }

    public static GameResultsController getInstance() {
        if (instance == null) {
            instance = new GameResultsController();
        }

        return instance;
    }

    public void doGetResults(Context context) {
        ParseObject.registerSubclass(GameResult.class);

        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ParseConnection connection = ParseConnection.getInstance();
        connection.addObserver((ParseConnectionObserver) context);
        connection.addObserver((ParseConnectionObserver) dialog);
        connection.obtainObjects("GameResult");
    }

    public void doSaveResults(Context context) {

    }
}
