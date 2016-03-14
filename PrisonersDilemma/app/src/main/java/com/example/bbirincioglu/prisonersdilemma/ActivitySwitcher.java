package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
