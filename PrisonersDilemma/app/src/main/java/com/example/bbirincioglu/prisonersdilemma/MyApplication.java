package com.example.bbirincioglu.prisonersdilemma;

import com.parse.Parse;

/**
 * The very first class which we initialize Parse features.
 */
public class MyApplication extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
