package com.example.bbirincioglu.prisonersdilemma;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.List;

public class GameResultsActivity extends AppCompatActivity implements ParseConnectionObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        GameResultsController controller = GameResultsController.getInstance();
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);
        dialogFactory.create(DialogFactory.DIALOG_PASSWORD).show();
        updateButtonSizes(0.45, 0);
    }

    private void updateButtonSizes(double widthRatio, double heightRatio) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int buttonWidth = (int) (screenWidth * widthRatio);
        int buttonHeight = (int) (screenHeight * heightRatio);
        int[] buttonIDs = new int[]{R.id.gameResultsGetResultsButton, R.id.gameResultsSaveResultsButton};

        if (buttonWidth != 0) {
            for (int buttonID : buttonIDs) {
                Button button = ((Button) findViewById(buttonID));
                button.setWidth(buttonWidth);
            }
        }

        if (buttonHeight != 0) {
            for (int buttonID : buttonIDs) {
                Button button = ((Button) findViewById(buttonID));
                button.setHeight(buttonHeight);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) {
            ListView listView = (ListView) findViewById(R.id.gameResultsListView);
            List<GameResult> gameResultList = (List) parseConnection.getObjects();

            for (int i = 0; i < gameResultList.size(); i++) {
                System.out.println("Game Result List: " + gameResultList.get(i).toString());
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.game_results_row, gameResultList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    System.out.println(textView.getText());
                }
            });

            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
        new ActivitySwitcher().fromPreviousToNext(this, MainMenuActivity.class, bundle, true);
    }

    public void onClick(View v) {
        int buttonID = v.getId();
        GameResultsController controller = GameResultsController.getInstance();

        if (buttonID == R.id.gameResultsGetResultsButton) {
            controller.doGetResults(this);
        } else if (buttonID == R.id.gameResultsSaveResultsButton) {
            controller.doSaveResults(this);
        }
    }
}