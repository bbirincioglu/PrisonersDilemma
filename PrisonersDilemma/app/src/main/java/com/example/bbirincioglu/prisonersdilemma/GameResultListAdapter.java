package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Another adapter for displaying game results in a list view with custom layout. Each row in the listView is constructed via getView() method.
 * The instance of this class is used in the GameResultsActivity.
 */
public class GameResultListAdapter extends ArrayAdapter<GameResult> {
    private Context context;
    private ArrayList<GameResult> gameResults;

    public GameResultListAdapter(Context context, int rowResourceID, ArrayList<GameResult> gameResults) {
        super(context, rowResourceID, gameResults);
        this.context = context;
        this.gameResults = gameResults;
    }

    //Each content of game result in the gameResults arrayList is inserted into a GUI object (View row) via this method.
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        row = inflater.inflate(R.layout.game_result_list_row, parent, false);
        GameResult gameResult = getGameResults().get(position);

        LinearLayout rowContainer = (LinearLayout) row.findViewById(R.id.game_result_list_row_container);
        int childCount = rowContainer.getChildCount();

        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) rowContainer.getChildAt(i);
            String hint = child.getHint().toString();
            Object value = gameResult.get(hint);

            if (value == null) {
                child.setText("null");
            } else {
                child.setText(value.toString());
            }
        }

        /*String p1Name = gameResult.getP1Name();
        String p1Surname = gameResult.getP1Surname();
        String p1Commitment = gameResult.getP1Commitment();
        String p1Decision = gameResult.getP1Decision();

        String p2Name = gameResult.getP2Name();
        String p2Surname = gameResult.getP2Surname();
        String p2Commitment = gameResult.getP2Commitment();
        String p2Decision = gameResult.getP2Decision();

        String copCop = gameResult.getCopCop();
        String copDef = gameResult.getCopDef();
        String defCop = gameResult.getDefCop();
        String defDef = gameResult.getDefDef();
        String punishment = gameResult.getPunishment();*/

        return row;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<GameResult> getGameResults() {
        return gameResults;
    }

    public void setGameResults(ArrayList<GameResult> gameResults) {
        this.gameResults = gameResults;
    }
}
