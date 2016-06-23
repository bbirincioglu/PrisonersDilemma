package com.example.bbirincioglu.prisonersdilemma;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.HashMap;

/**
 * Controller for GamePlayActivity.
 */
public class GamePlayController {
    public static final String CHOICE_COOPERATE = "COOPERATE";
    public static final String CHOICE_DEFECT = "DEFECT";
    private ConnectedThread connectedThread; //this is for communicating with other phone using bluetooth socket, and corresponding input-output streams.

    public GamePlayController() {

    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public void doWrite(Object message) {
        getConnectedThread().write(message);
    }

    /*
        Saves commitment of the player to Parse server.
     */
    public void doSaveCommitment(Context context, ParseConnection parseConnection, String commitment) {
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo()); //According to game number, get
                                                                                                                                        // game result object which points
                                                                                                                                        //to a specific row in the database.
        System.out.println("GAME NUMBER: " + gameResult.getGameNo());

        if (SocketSingleton.getInstance().isHosted()) { //Check whether it is first player (Hosted) or second player (Client).
            gameResult.setP1Commitment(commitment);
        } else {
            gameResult.setP2Commitment(commitment);
        }

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection, "committed"); //Not only we save commitment during the process, we also
                                                                                        //save name, surname of the player together with settings of the game.
    }

    /*  Saves decision of the player to Parse server.
    * */
    public void doSaveDecision(Context context, ParseConnection parseConnection, String decision) {
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        System.out.println("GAME NUMBER: " + gameResult.getGameNo());

        if (SocketSingleton.getInstance().isHosted()) { //Check whether it is first player (Hosted) or second player (Client).
            gameResult.setP1Decision(decision);
        } else {
            gameResult.setP2Decision(decision);
        }

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection, "decided"); //Not only we save decision during the process, we also
                                                                                    //save name, surname of the player together with settings of the game.
    }

    private void doSaveNameSurnameAndSettings(GameResult gameResult, final Context context, ParseConnection parseConnection, final String writeText) {
        GameSettings gameSettings = GameSettings.loadFromPreferences(context); //create GameSettings instance from Settings Preferences.
        SharedPreferences sp = context.getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE); //get another Preferences which stores name, and surname.
        String name = sp.getString(Keys.PLAYER_NAME, "Default");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "Default");

        if (SocketSingleton.getInstance().isHosted()) { //First Player
            gameResult.setP1Name(name);
            gameResult.setP1Surname(surname);
        } else { //Second Player
            gameResult.setP2Name(name);
            gameResult.setP2Surname(surname);
        }

        //map game settings' values to game result object.
        gameResult.setCopCop(gameSettings.getCopCop());
        gameResult.setCopDef(gameSettings.getCopDef());
        gameResult.setDefCop(gameSettings.getDefCop());
        gameResult.setDefDef(gameSettings.getDefDef());
        gameResult.setWithCommitment(gameSettings.getWithCommitment());
        gameResult.setPunishment(gameSettings.getPunishment());
        gameResult.saveInBackground(new SaveCallback() { //save game result with a call back (listener which triggers when the saving process completes).
            @Override
            public void done(ParseException e) {
                if (writeText.equals("decided")) { //The player decides instead of commits, thus this means we also notify message handler to close dialogs.
                    final GamePlayActivity gamePlayActivity = ((GamePlayActivity) context);
                    gamePlayActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gamePlayActivity.getParseConnection().setMyDecisionSaved(true);
                            gamePlayActivity.getMessageHandler().notifyObservers();
                        }
                    });
                }

                getConnectedThread().write(writeText); //Notify other player to emphasize that our saving process is completed. By this way, other player
                                                        //can continue to access database.
            }
        });
    }

    //Display game result via instance of GameResultDialog.
    public void doDisplayGameResult(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance(); //get DialogFactory object.
        dialogFactory.setContext(context); //set its context so that dialogFactory knows in which activity a dialog will be created.

        final GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT); //create dialog.
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo()); //get game no
        gameResultDialog.injectContent(gameResult); //gameResult object contains all the information about players, and game settings. gameResultDialog is used
                                                    //to display content of the gameResult object in GUI. Thus we "inject" content of gameResult into gameResultDialog.

        final Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB); //we also send final results to server, thus create background job dialog.
        ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText("Sending Results To Server..."); //with proper message.
        dialog.show(); //display background job dialog.

        gameResult.saveInBackground(new SaveCallback() { //when saving process completes, done(ParseException e) triggers
            @Override
            public void done(ParseException e) {
                if (e == null) { //There is nothing went wrong.
                    dialog.dismiss(); //Game result is saved, thus close background job dialog.
                    gameResultDialog.show(); //And show gameResultDialog
                } else { //An Exception occured.
                    ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText(e.getLocalizedMessage()); //Display error message.
                }
            }
        });
    }
}
