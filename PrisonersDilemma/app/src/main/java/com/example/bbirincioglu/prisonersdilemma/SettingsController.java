package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Controller class for SettingsActivity. It also has all the listeners for GUI objects of the SettingsActivity.
 */
public class SettingsController implements TextWatcher, View.OnClickListener{
    private static SettingsController instance;
    private GameSettings gameSettings;

    private SettingsController() {
        setGameSettings(null);
    }

    public static SettingsController getInstance() {
        if (instance == null) {
            instance = new SettingsController();
        }

        return instance;
    }

    @Override
    public void onClick(View v) { //Listens for check boxes, and buttons
        int id = v.getId();
        Context context = v.getContext();
        Activity activity = (Activity) context;

        if (id == R.id.withCommitmentCheckBox) {
            CheckBox withCommitmentCheckBox = ((CheckBox) v);
            int punishmentVisibility;

            if (withCommitmentCheckBox.isChecked()) { //it is checked, thus make punishment edit text visible so that player can enter a punishment value.
                punishmentVisibility = View.VISIBLE;
            } else {
                punishmentVisibility = View.GONE; //it is not checked, disappear punishment edit text.
            }

            activity.findViewById(R.id.punishmentEditText).setVisibility(punishmentVisibility);
        } else if (id == R.id.settingsSaveButton){
            String copCop = ((EditText) activity.findViewById(R.id.copCop)).getText().toString();
            String copDef = ((EditText) activity.findViewById(R.id.copDef)).getText().toString();
            String defCop = ((EditText) activity.findViewById(R.id.defCop)).getText().toString();
            String defDef = ((EditText) activity.findViewById(R.id.defDef)).getText().toString();
            String withCommitment = String.valueOf(((CheckBox) activity.findViewById(R.id.withCommitmentCheckBox)).isChecked());
            String punishment = ((EditText) activity.findViewById(R.id.punishmentEditText)).getText().toString();

            if (isValidInput(copCop) && isValidInput(copDef) && isValidInput(defCop) && isValidInput(defDef)) {
                GameSettings settings = getGameSettings();
                settings.setCopCop(copCop);
                settings.setCopDef(copDef);
                settings.setDefCop(defCop);
                settings.setDefDef(defDef);
                settings.setWithCommitment(withCommitment);
                settings.setPunishment(punishment);
                getGameSettings().saveIntoPreferences();

            /*Bundle bundle = new Bundle();
            bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
            new ActivitySwitcher().fromPreviousToNext(activity, MainMenuActivity.class, bundle, true);*/
            } else {
                TextView textView = ((TextView) activity.findViewById(R.id.errorMessageTextView));
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();

        if (text != null && !text.equals("")) {
            String[] data = checkInvalidChars(text);
            boolean hasInvalidChars = Boolean.valueOf(data[0]);

            if (hasInvalidChars) {
                s.clear();
                s.append(data[1]);
            }
        }
    }

    //removes invalid characters from text.
    private String[] checkInvalidChars(String text) {
        String[] data = new String[2];
        String validChars = "0123456789-,";
        data[0] = "false";
        data[1] = "";

        int length = text.length();

        for (int i = 0; i < length; i++) {
            char charAtI = text.charAt(i);

            if (validChars.contains("" + charAtI)) {
                data[1] = data[1] + charAtI;
            } else {
                data[0] = "true";
            }
        }

        return data;
    }

    private boolean isValidInput(String text) {
        boolean result = true;

        if (text != null && !text.equals("")) {
            ArrayList<String> subStrings = split(text, ',');

            if (subStrings.size() != 2) {
                result = false;
            } else {
                String first = subStrings.get(0);
                String second = subStrings.get(1);

                if (!isNumeric(first) || !isNumeric(second)) {
                    result = false;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    //divides text according to "splitWith" character, and returns substrings of "text" in string array format.
    private ArrayList<String> split(String text, char splitWith) {
        ArrayList<String> subStrings = new ArrayList<String>();
        int length = text.length();
        String temp = "";

        for (int i = 0; i < length; i++) {
            char charAtI = text.charAt(i);

            if (charAtI == splitWith) {
                subStrings.add(temp);
                temp = "";
            } else {
                temp += charAtI;
            }
        }

        if (!temp.equals("")) {
            subStrings.add(temp);
        }

        return subStrings;
    }

    //check whether text is an integer or not.
    private boolean isNumeric(String text) {
        boolean result;

        try {
            Integer.valueOf(text);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
