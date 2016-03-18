package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by bbirincioglu on 2/28/2016.
 */
public class ActivitySwitcher {
    public ActivitySwitcher() {

    }

    public void fromPreviousToNext(Activity previous, Class next, Bundle data, boolean isKillPrevious) {
        ArrayList<Dialog> dialogs = null;

        if (previous instanceof BluetoothGameActivity) {
            dialogs = ((BluetoothGameActivity) previous).getDialogs();
        } else if (previous instanceof GamePlayActivity) {
            dialogs = ((GamePlayActivity) previous).getDialogs();
        }

        if (dialogs != null) {
            for (Dialog dialog : dialogs) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }

        Intent intent = new Intent(previous, next);

        if (data != null) {
            intent.putExtras(data);
        }

        if (isKillPrevious) {
            previous.finish();
        }

        previous.startActivity(intent);
    }
}
