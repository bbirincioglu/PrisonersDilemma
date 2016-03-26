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
 * Created by bbirincioglu on 3/14/2016.
 */
public class GamePlayController {
    public static final String CHOICE_COOPERATE = "COOPERATE";
    public static final String CHOICE_DEFECT = "DEFECT";
    private ConnectedThread connectedThread;

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

    public void doSaveCommitment(Context context, ParseConnection parseConnection, String commitment) {
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        System.out.println("GAME NUMBER: " + gameResult.getGameNo());

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setP1Commitment(commitment);
        } else {
            gameResult.setP2Commitment(commitment);
        }

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection, "committed");
    }

    public void doSaveDecision(Context context, ParseConnection parseConnection, String decision) {
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        System.out.println("GAME NUMBER: " + gameResult.getGameNo());

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setP1Decision(decision);
        } else {
            gameResult.setP2Decision(decision);
        }

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection, "decided");
    }

    private void doSaveNameSurnameAndSettings(GameResult gameResult, final Context context, ParseConnection parseConnection, final String writeText) {
        GameSettings gameSettings = GameSettings.loadFromPreferences(context);
        SharedPreferences sp = context.getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        String name = sp.getString(Keys.PLAYER_NAME, "Default");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "Default");

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setP1Name(name);
            gameResult.setP1Surname(surname);
        } else {
            gameResult.setP2Name(name);
            gameResult.setP2Surname(surname);
        }

        gameResult.setCopCop(gameSettings.getCopCop());
        gameResult.setCopDef(gameSettings.getCopDef());
        gameResult.setDefCop(gameSettings.getDefCop());
        gameResult.setDefDef(gameSettings.getDefDef());
        gameResult.setWithCommitment(gameSettings.getWithCommitment());
        gameResult.setPunishment(gameSettings.getPunishment());
        gameResult.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (writeText.equals("decided")) {
                    final GamePlayActivity gamePlayActivity = ((GamePlayActivity) context);
                    gamePlayActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gamePlayActivity.getParseConnection().setMyDecisionSaved(true);
                            gamePlayActivity.getMessageHandler().notifyObservers();
                        }
                    });
                }

                getConnectedThread().write(writeText);
            }
        });
    }

    public void doDisplayGameResult(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);

        final GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        gameResultDialog.injectContent(gameResult);

        final Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText("Sending Results To Server...");
        dialog.show();

        gameResult.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    gameResultDialog.show();
                } else {
                    ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText(e.getLocalizedMessage());
                }
            }
        });
    }
}
