package com.example.bbirincioglu.prisonersdilemma;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * A custom class for storing / receiving game result to / from Parse server.
 */
@ParseClassName("GameResult")
public class GameResult extends ParseObject {
    public static final String[] HEADERS = new String[]{"GAME_NO", "P1_NAME", "P1_SURNAME", "P1_COMMITMENT", /*Column Names while extracting to excel.
                                                                                     They are not used while obtaining column values from database table.
                                                                                     Instead of those we use Keys.RATIO, Keys.INITIAL_TOTAL etc.*/
            "P1_DECISION", "P1_PAYOFF", "P2_NAME", "P2_SURNAME", "P2_COMMITMENT",
            "P2_DECISION", "P2_PAYOFF", "COP_COP", "COP_DEF", "DEF_COP", "DEF_DEF",
            "WITH_COMMITMENT", "PUNISHMENT"};
    public static final String SPLIT_WITH = "___";

    public GameResult() {
        super();
    }

    //In the database, we have two tables (GameNo, and GameResult). GameNo has only one row which stores current game number. In order to create a row
    //in the GameResult table, we need to receive game number from GameNo table first, and create new record in the GameResult table with this game number.
    //Finally we increase the game number in the GameNo table.
    public void obtainGameNo() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameNo");

        try {
            ParseObject parseObject = query.get("y5cXloLDWA"); //Object ID for selecting row in the GameNo table.
            setGameNo(parseObject.getInt(Keys.GAME_NO));
            parseObject.put(Keys.GAME_NO, getGameNo() + 1);
            parseObject.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Key value pairs for setting and getting values from this object.
    public String getID() {
        return getString(Keys.PARSE_OBJECT_ID);
    }

    public void setID(String id) {
        put(Keys.PARSE_OBJECT_ID, id);
    }

    public int getGameNo() {
        return getInt(Keys.GAME_NO);
    }

    public void setGameNo(int gameNo) {
        put(Keys.GAME_NO, gameNo);
    }

    public String getP1Name() {
        return getString(Keys.PLAYER_1_NAME);
    }

    public void setP1Name(String p1Name) {
        put(Keys.PLAYER_1_NAME, p1Name);
    }

    public String getP1Surname() {
        return getString(Keys.PLAYER_1_SURNAME);
    }

    public void setP1Surname(String p1Surname) {
        put(Keys.PLAYER_1_SURNAME, p1Surname);
    }

    public String getP1Commitment() {
        return getString(Keys.PLAYER_1_COMMITMENT);
    }

    public void setP1Commitment(String p1Commitment) { put(Keys.PLAYER_1_COMMITMENT, p1Commitment); }

    public String getP1Decision() {
        return getString(Keys.PLAYER_1_DECISION);
    }

    public void setP1Decision(String p1Decision) {
        put(Keys.PLAYER_1_DECISION, p1Decision);
    }

    public String getP2Name() {
        return getString(Keys.PLAYER_2_NAME);
    }

    public void setP2Name(String p2Name) {
        put(Keys.PLAYER_2_NAME, p2Name);
    }

    public String getP2Surname() {
        return getString(Keys.PLAYER_2_SURNAME);
    }

    public void setP2Surname(String p2Surname) {
        put(Keys.PLAYER_2_SURNAME, p2Surname);
    }

    public String getP2Commitment() {
        return getString(Keys.PLAYER_2_COMMITMENT);
    }

    public void setP2Commitment(String p2Commitment) { put(Keys.PLAYER_2_COMMITMENT, p2Commitment); }

    public String getP2Decision() {
        return getString(Keys.PLAYER_2_DECISION);
    }

    public void setP2Decision(String p2Decision) {
        put(Keys.PLAYER_2_DECISION, p2Decision);
    }

    public String getCopCop() {
        return getString(Keys.COOPERATE_COOPERATE);
    }

    public void setCopCop(String copCop) {
        put(Keys.COOPERATE_COOPERATE, copCop);
    }

    public String getCopDef() {
        return getString(Keys.COOPERATE_DEFECT);
    }

    public void setCopDef(String copDef) {
        put(Keys.COOPERATE_DEFECT, copDef);
    }

    public String getDefCop() {
        return getString(Keys.DEFECT_COOPERATE);
    }

    public void setDefCop(String defCop) {
        put(Keys.DEFECT_COOPERATE, defCop);
    }

    public String getDefDef() {
        return getString(Keys.DEFECT_DEFECT);
    }

    public void setDefDef(String defDef) {
        put(Keys.DEFECT_DEFECT, defDef);
    }

    public String getWithCommitment() {
        return getString(Keys.WITH_COMMITMENT);
    }

    public void setWithCommitment(String withCommitment) {
        put(Keys.WITH_COMMITMENT, withCommitment);
    }

    public void setPunishment(String punishment) {
        put(Keys.PUNISHMENT, punishment);
    }

    public String getPunishment() {
        return getString(Keys.PUNISHMENT);
    }

    public void setP1Payoff(String p1Payoff) {
        put(Keys.PLAYER_1_PAYOFF, p1Payoff);
    }

    public String getP1Payoff() {
        return getString(Keys.PLAYER_1_PAYOFF);
    }

    public void setP2Payoff(String p2Payoff) {
        put(Keys.PLAYER_2_PAYOFF, p2Payoff);
    }

    public String getP2Payoff() {
        return getString(Keys.PLAYER_2_PAYOFF);
    }

    public void saveGameResultAsHost() {
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //send signal to join player to retreive last row and send its data.
                }
            }
        });
    }

    public void saveGameResultAsGuest() {
        saveInBackground();
    }

    public String toString() {
        String gameResultAsString;
        gameResultAsString = getGameNo() + SPLIT_WITH
                + getP1Name() + SPLIT_WITH
                + getP1Surname() + SPLIT_WITH
                + getP1Commitment() + SPLIT_WITH
                + getP1Decision() + SPLIT_WITH
                + getP1Payoff() + SPLIT_WITH
                + getP2Name() + SPLIT_WITH
                + getP2Surname() + SPLIT_WITH
                + getP2Commitment() + SPLIT_WITH
                + getP2Decision() + SPLIT_WITH
                + getP2Payoff() + SPLIT_WITH
                + getCopCop() + SPLIT_WITH
                + getCopDef() + SPLIT_WITH
                + getDefCop() + SPLIT_WITH
                + getDefDef() + SPLIT_WITH
                + getWithCommitment() + SPLIT_WITH
                + getPunishment() + SPLIT_WITH;
        return gameResultAsString;
    }
}
