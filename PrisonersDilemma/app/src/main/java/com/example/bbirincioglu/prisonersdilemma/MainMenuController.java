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

import java.util.Set;

/**
 * Created by bbirincioglu on 2/28/2016.
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

    public void doCheckPlayerInfo(PlayerInfoDialog dialog, String name, String surname) {
        TextView errorMessageTextView = (TextView) dialog.findViewById(R.id.errorMessageTextView);

        if (name == null || surname == null || name.equals("") || surname.equals("")) {
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            String capitalizedName = capitalizeFirstLetter(name);
            String capitalizedSurname = capitalizeFirstLetter(surname);
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

    private String capitalizeFirstLetter(String text) {
        String capitalizedText = null;
        char firstChar = text.charAt(0);
        capitalizedText = Character.toUpperCase(firstChar) + text.substring(1, text.length());
        return capitalizedText;
    }

    public void doEnableWifi(Context context) {
        InternetHandler internetHandler = new InternetHandler(context);
        internetHandler.enableWifi();
    }

    public void doEnableMobileData(Context context) {
        InternetHandler internetHandler = new InternetHandler(context);
        internetHandler.setEnableMobileData(true);
    }
}
