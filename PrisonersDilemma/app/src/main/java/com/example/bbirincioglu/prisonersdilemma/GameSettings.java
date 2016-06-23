package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The class for storing settings of the game, and retreiving from android's built-in Preferences using KEYS array.
 */
public class GameSettings implements Serializable {
    public static final String[] KEYS = new String[]{"copCop", "copDef", "defCop", "defDef", "withCommitment", "punishment"};
    private Context context;
    private String copCop;
    private String copDef;
    private String defCop;
    private String defDef;
    private String withCommitment;
    private String punishment;

    public GameSettings(Context context, String copCop, String copDef, String defCop, String defDef, String withCommitment, String punishment) {
        this.context = context;
        this.copCop = copCop;
        this.copDef = copDef;
        this.defCop = defCop;
        this.defDef = defDef;
        this.withCommitment = withCommitment;
        this.punishment = punishment;
    }

    //Load values from Preferences.
    public static GameSettings loadFromPreferences(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Keys.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        String copCop = sp.getString(Keys.COOPERATE_COOPERATE, "-1,-1");
        String copDef = sp.getString(Keys.COOPERATE_DEFECT, "-3,0");
        String defCop = sp.getString(Keys.DEFECT_COOPERATE, "0,-3");
        String defDef = sp.getString(Keys.DEFECT_DEFECT, "-2,-2");
        String withCommitment = sp.getString(Keys.WITH_COMMITMENT, "false");
        String punishment = sp.getString(Keys.PUNISHMENT, "0");
        return new GameSettings(context, copCop, copDef, defCop, defDef, withCommitment, punishment);
    }

    //Save values to Preferences.
    public void saveIntoPreferences() {
        SharedPreferences sp = getContext().getSharedPreferences(Keys.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sp.edit();
        editor.putString(Keys.COOPERATE_COOPERATE, getCopCop());
        editor.putString(Keys.COOPERATE_DEFECT, getCopDef());
        editor.putString(Keys.DEFECT_COOPERATE, getDefCop());
        editor.putString(Keys.DEFECT_DEFECT, getDefDef());
        editor.putString(Keys.WITH_COMMITMENT, getWithCommitment());
        editor.putString(Keys.PUNISHMENT, getPunishment());
        editor.commit();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCopCop() {
        return copCop;
    }

    public void setCopCop(String copCop) {
        this.copCop = copCop;
    }

    public String getCopDef() {
        return copDef;
    }

    public void setCopDef(String copDef) {
        this.copDef = copDef;
    }

    public String getDefCop() {
        return defCop;
    }

    public void setDefCop(String defCop) {
        this.defCop = defCop;
    }

    public String getDefDef() {
        return defDef;
    }

    public void setDefDef(String defDef) {
        this.defDef = defDef;
    }

    public String getWithCommitment() {
        return withCommitment;
    }

    public void setWithCommitment(String withCommitment) {
        this.withCommitment = withCommitment;
    }

    public String getPunishment() {
        return punishment;
    }

    public void setPunishment(String punishment) {
        this.punishment = punishment;
    }

    //Converts this object to hashMap so that it can be written in to bluetoothSocket, and sent to other player.
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String[] KEYS = GameSettings.KEYS;
        hashMap.put(KEYS[0], getCopCop());
        hashMap.put(KEYS[1], getCopDef());
        hashMap.put(KEYS[2], getDefCop());
        hashMap.put(KEYS[3], getDefDef());
        hashMap.put(KEYS[4], getWithCommitment());
        hashMap.put(KEYS[5], getPunishment());
        return hashMap;
    }

    //Converts hashMap representation to actual GameSettings object, and returns it.
    public static GameSettings fromHashMap(Context context, HashMap<String, String> hashMap) {
        String[] KEYS = GameSettings.KEYS;
        String copCop = hashMap.get(KEYS[0]);
        String copDef = hashMap.get(KEYS[1]);
        String defCop = hashMap.get(KEYS[2]);
        String defDef = hashMap.get(KEYS[3]);
        String withCommitment = hashMap.get(KEYS[4]);
        String punishment = hashMap.get(KEYS[5]);
        return new GameSettings(context, copCop, copDef, defCop, defDef, withCommitment, punishment);
    }
}