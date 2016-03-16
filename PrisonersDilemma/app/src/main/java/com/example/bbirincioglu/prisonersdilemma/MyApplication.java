package com.example.bbirincioglu.prisonersdilemma;

import com.parse.Parse;

/**
 * Created by bbirincioglu on 3/16/2016.
 */
public class MyApplication extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
