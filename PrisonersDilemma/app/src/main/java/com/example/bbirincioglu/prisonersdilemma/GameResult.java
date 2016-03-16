package com.example.bbirincioglu.prisonersdilemma;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by bbirincioglu on 3/6/2016.
 */
@ParseClassName("GameResult")
public class GameResult extends ParseObject {
    public GameResult() {
        super();
    }

    public void obtainGameNo() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameNo");

        try {
            ParseObject parseObject = query.get("dkMukfeuZN");
            setGameNo(parseObject.getInt(Keys.GAME_NO));
            parseObject.put(Keys.GAME_NO, getGameNo() + 1);
            parseObject.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
        int gameNo = getGameNo();
        String p1Name = getP1Name();
        String p1Surname = getP1Surname();
        String p1Commitment = getP1Commitment();
        String p2Name = getP2Name();
        String p2Surname = getP2Surname();
        String p2Commitment = getP2Commitment();
        String copCop = getCopCop();
        String copDef = getCopDef();
        String defCop = getDefCop();
        String defDef = getDefDef();
        gameResultAsString = "{" + gameNo + ";" + p1Name + ";" + p1Surname + ";"
                                + p1Commitment + ";" + p2Name + ";" + p2Surname + ";"
                                + p2Commitment + ";" + copCop + ";" + copDef + ";"
                                + defCop + ";" + defDef + "}";
        return gameResultAsString;
    }
}
