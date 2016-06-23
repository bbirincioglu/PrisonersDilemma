package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;

/**
 * Interface implemented by all the actual dialog classes.
 */
public interface SimpleDialog {
    public static final String PLAYER_INFO_DIALOG = "playerInfoDialog";
    public static final String DISCOVERY_LIST_DIALOG = "discoveryListDialog";
    public void initialize();
}
