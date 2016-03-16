package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;

/**
 * Created by bbirincioglu on 3/14/2016.
 */
public class GamePlayController {
    public static final String CHOICE_COOPERATE = "COOPERATE";
    public static final String CHOICE_DEFECT = "DEFECT";
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

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection);
        getConnectedThread().write("committed");
    }

    public void doSaveDecision(Context context, ParseConnection parseConnection, String decision) {
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        System.out.println("GAME NUMBER: " + gameResult.getGameNo());

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setP1Decision(decision);
        } else {
            gameResult.setP2Decision(decision);
        }

        doSaveNameSurnameAndSettings(gameResult, context, parseConnection);
        getConnectedThread().write("decided");
    }

    private void doSaveNameSurnameAndSettings(GameResult gameResult, Context context, ParseConnection parseConnection) {
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
        gameResult.saveInBackground();
    }

    public void doDisplayGameResult(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        gameResultDialog.injectContent(gameResult);
        gameResultDialog.show();
    }
}
