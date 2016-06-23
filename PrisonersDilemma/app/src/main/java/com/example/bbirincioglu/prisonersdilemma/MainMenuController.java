package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

/**
 * Controller for Main Menu.
 */
public class MainMenuController {
    private static MainMenuController instance;
    private AnimationHandler animationHandler;
    private ActivitySwitcher activitySwitcher;
    private BluetoothHandler bluetoothHandler;

    private MainMenuController() {
        setAnimationHandler(new AnimationHandler());
        setActivitySwitcher(new ActivitySwitcher());
        setBluetoothHandler(new BluetoothHandler());
    }

    public static MainMenuController getInstance() {
        if (instance == null) {
            instance = new MainMenuController();
        }

        return instance;
    }

    //If devices discoverability is not enabled, enable it if device does support such feature, else display informative message "Your device doesn't...".
    public void doEnableDiscoverability(Context context) {
        BluetoothHandler bluetoothHandler = getBluetoothHandler();

        if (bluetoothHandler.isBluetoothSupported()) {
            if (!bluetoothHandler.isDiscoverable()) {
                bluetoothHandler.enableDiscoverability(context);
            }
        } else {
            InformativeDialog dialog = (InformativeDialog) DialogFactory.getInstance().create(DialogFactory.DIALOG_INFORMATIVE);
            dialog.setText("Your device doesn't support bluetooth.");
            dialog.show();
        }
    }

    public void listPairedDevices(Set<BluetoothDevice> bluetoothDevices) {
        System.out.println("IN THE LIST PAIRED DEVICES.");
    }

    //Switch from MainMenuActivity to BluetoothGameActivity, and animate related button while switching.
    public void doBluetoothGameActivity(View v) {
        int invalid = AnimationHandler.INVALID;
        long animationDuration = getAnimationHandler().animateOn(v, new int[]{R.anim.fade_out, R.anim.scale_up}, invalid, invalid, invalid);

        final Activity previous = (Activity) v.getContext();
        v.postDelayed(new Runnable() {
            public void run() {
                getActivitySwitcher().fromPreviousToNext(previous, BluetoothGameActivity.class, null, true);
            }
        }, (long) (animationDuration / 1.2));
    }

    //Switch from MainMenuActivity to SettingsActivity, and animate related button while switching.
    public void doSettingsActivity(View v) {
        int invalid = AnimationHandler.INVALID;
        long animationDuration = getAnimationHandler().animateOn(v, new int[]{R.anim.fade_out, R.anim.scale_up}, invalid, invalid, invalid);

        final Activity previous = (Activity) v.getContext();
        v.postDelayed(new Runnable() {
            public void run() {
                getActivitySwitcher().fromPreviousToNext(previous, SettingsActivity.class, null, true);
            }
        }, (long) (animationDuration / 1.2));
    }

    //Switch from MainMenuActivity to GameResultsActivity, and animate related button while switching.
    public void doGameResultsActivity(View v) {
        int invalid = AnimationHandler.INVALID;
        long animationDuration = getAnimationHandler().animateOn(v, new int[]{R.anim.fade_out, R.anim.scale_up}, invalid, invalid, invalid);

        final Activity previous = (Activity) v.getContext();
        v.postDelayed(new Runnable() {
            public void run() {
                getActivitySwitcher().fromPreviousToNext(previous, GameResultsActivity.class, null, true);
            }
        }, (long) (animationDuration / 1.2));
    }

    //Close the game.
    public void doExitGame(Context context) {
        ((Activity) context).finish();
    }

    public void setAnimationHandler(AnimationHandler animationHandler) {
        this.animationHandler = animationHandler;
    }

    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }

    public void setActivitySwitcher(ActivitySwitcher activitySwitcher) {
        this.activitySwitcher = activitySwitcher;
    }

    public ActivitySwitcher getActivitySwitcher() {
        return activitySwitcher;
    }

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }

    public void setBluetoothHandler(BluetoothHandler bluetoothHandler) {
        this.bluetoothHandler = bluetoothHandler;
    }

    //Check whether player correctly enters his name, and surname. If they are entered correctly, then save them into a preference with name
    //Keys.PLAYER_INFO_PREFERENCES. Otherwise, display error message.
    public void doCheckPlayerInfo(PlayerInfoDialog dialog, String nameSurname) {
        TextView errorMessageTextView = (TextView) dialog.findViewById(R.id.errorMessageTextView);
        String[] subStrings = mySplit(nameSurname, " ");

        if (subStrings.length != 2 || subStrings[0].length() < 2 || subStrings[1].length() < 2) {
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            String capitalizedName = capitalizeFirstLetter(subStrings[0]);
            String capitalizedSurname = capitalizeFirstLetter(subStrings[1]);
            SharedPreferences sharedPreferences = dialog.getActivity().getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Keys.PLAYER_NAME, capitalizedName);
            editor.putString(Keys.PLAYER_SURNAME, capitalizedSurname);
            editor.apply();

            dialog.cancel();
            MainMenuController controller = MainMenuController.getInstance();
            controller.doEnableDiscoverability(dialog.getActivity());
        }
    }

    private String[] mySplit(String text, String splitWith) {
        ArrayList<String> subStrings = new ArrayList<String>();
        int length = text.length();
        String temp = "";

        for (int i = 0; i < length; i++) {
            char charAtI = text.charAt(i);

            if ((charAtI + "").equals(splitWith)) {
                subStrings.add(temp);
                temp = "";
            } else {
                temp += charAtI;
            }
        }

        if (!temp.equals("")) {
            subStrings.add(temp);
        }

        String[] tempArray = new String[subStrings.size()];
        length = tempArray.length;

        for (int i = 0; i < length; i++) {
            tempArray[i] = subStrings.get(i);
        }

        return tempArray;
    }

    //For Capitalizing First Letter of a String.
    private String capitalizeFirstLetter(String text) {
        String capitalizedText = null;
        char firstChar = text.charAt(0);
        capitalizedText = Character.toUpperCase(firstChar) + text.substring(1, text.length());
        return capitalizedText;
    }

    //For Enabling Wifi.
    public void doEnableWifi(Context context) {
        InternetHandler internetHandler = new InternetHandler(context);
        internetHandler.enableWifi();
    }

    //For Enabling Mobile Data.
    public void doEnableMobileData(Context context) {
        InternetHandler internetHandler = new InternetHandler(context);
        internetHandler.setEnableMobileData(true);
    }
}
