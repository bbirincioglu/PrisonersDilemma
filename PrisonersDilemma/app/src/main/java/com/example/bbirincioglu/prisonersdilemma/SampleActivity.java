package com.example.bbirincioglu.prisonersdilemma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Random;
//Dummy Activity. Don't Take Into Consideration.
public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
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

    public void onClick(View v) {
        Button button = (Button) v;

        if (button.getText().toString().equals("SEND")) {
            ParseObject gameResult = new ParseObject("GameResult");
            gameResult.add("Column1", String.valueOf(new Random().nextInt(50)));
            gameResult.saveInBackground();
        } else {
            System.out.println("In the receive");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GameResult");
            List<ParseObject> gameResults = null;

            try {
                gameResults = query.find();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < gameResults.size(); i++) {
               System.out.println(gameResults.get(i).get("Column1").toString());
            }
        }
    }
}
