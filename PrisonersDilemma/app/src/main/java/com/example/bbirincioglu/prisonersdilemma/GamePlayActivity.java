package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.Arrays;

public class GamePlayActivity extends AppCompatActivity {
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setMessageHandler(new MessageHandler(this));

        SocketSingleton socketSingleton = SocketSingleton.getInstance();
        GamePlayController gamePlayController = GamePlayController.getInstance();
        gamePlayController.setConnectedThread(new ConnectedThread(this, socketSingleton.getSocket(), getMessageHandler()));
        gamePlayController.getConnectedThread().start();

        SharedPreferences sp = getSharedPreferences(Keys.DUMMY_PREFERENCES, Context.MODE_PRIVATE);
        String playerName = sp.getString(Keys.NAME, "DEFAULTTTT");
        gamePlayController.doWrite(playerName);

        if (socketSingleton.isHosted()) {
            //serialize your game settings object.
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
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

    public class MessageHandler extends Handler {
        private Context context;

        public MessageHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String messageAsString = null;

            try {
                messageAsString = new String((byte[]) msg.obj, 0, msg.arg1);
                System.out.println("MESSAGE AS STRING: " + messageAsString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
