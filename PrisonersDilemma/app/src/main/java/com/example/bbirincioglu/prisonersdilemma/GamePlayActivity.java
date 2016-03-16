package com.example.bbirincioglu.prisonersdilemma;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.Parse;
import com.parse.ParseObject;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePlayActivity extends AppCompatActivity {
    private MessageHandler messageHandler;
    private ParseConnection parseConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        ParseObject.registerSubclass(GameResult.class);
        setParseConnection(ParseConnection.getNewInstance());

        setMessageHandler(new MessageHandler(this));
        SocketSingleton socketSingleton = SocketSingleton.getInstance();
        GamePlayController gamePlayController = GamePlayController.getInstance();
        gamePlayController.setConnectedThread(new ConnectedThread(this, socketSingleton.getSocket(), getMessageHandler()));
        gamePlayController.getConnectedThread().start();

        if (socketSingleton.isHosted()) {
            System.out.println("IS HOSTED.");
            GameSettings gameSettings = GameSettings.loadFromPreferences(this);
            gamePlayController.doWrite(gameSettings.toHashMap());
            displayValues(gameSettings);

            getParseConnection().createEmptyGameResult();
        } else {
            System.out.println("IS NOT HOSTED.");
        }

        disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.settingsLinearLayout), false);
        attachListeners();
    }

    private void attachListeners() {
        ButtonListener buttonListener = new ButtonListener();
        findViewById(R.id.commitToCooperateButton).setOnClickListener(buttonListener);
        findViewById(R.id.commitToDefectButton).setOnClickListener(buttonListener);
        findViewById(R.id.decideToCooperateButton).setOnClickListener(buttonListener);
        findViewById(R.id.decideToDefectButton).setOnClickListener(buttonListener);
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

    public interface MessageHandlerObserver {
        public void update(MessageHandler messageHandler);
    }

    public class MessageHandler extends Handler {
        public static final String STATE_COMMITMENT = "commitment";
        public static final String STATE_DECISION = "decision";

        private Context context;
        private boolean isGameWithCommitment;
        private boolean isOtherPlayerCommitted;
        private boolean isOtherPlayerDecided;
        private String currentState;
        private ArrayList<MessageHandlerObserver> observers;

        public MessageHandler(Context context) {
            this.context = context;
            this.isOtherPlayerCommitted = false;
            this.isOtherPlayerDecided = false;
            this.isGameWithCommitment = false;
            this.currentState = null;
            this.observers = new ArrayList<MessageHandlerObserver>();
        }

        public String getCurrentState() {
            return currentState;
        }

        public void setCurrentState(String currentState) {
            this.currentState = currentState;
        }

        @Override
        public void handleMessage(Message msg) {
            String messageAsString = null;
            HashMap<String, String> messageAsGameSettings = null;

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
                ObjectInputStream ois = new ObjectInputStream(bais);
                messageAsGameSettings = (HashMap<String, String>) ois.readObject();
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
                    messageAsString = new String((byte[]) msg.obj, 0, msg.arg1);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            if (messageAsString != null) {
                if (messageAsString.contains("gameNoOnly:")) {
                    System.out.println("IN THE GAME NO ONLY.");
                    getParseConnection().setCurrentGameNo(Integer.valueOf(messageAsString.split(":")[1]));
                    System.out.println("IN THE GAME NO ONLY: " + getParseConnection().getCurrentGameNo());
                } else if (messageAsString.equals("committed")){
                    setIsOtherPlayerCommitted(true);
                } else if (messageAsString.equals("decided")) {
                    setIsOtherPlayerDecided(true);
                }
            } else if (messageAsGameSettings != null) {
                GameSettings gameSettings = GameSettings.fromHashMap(getContext(), messageAsGameSettings);
                gameSettings.saveIntoPreferences();
                displayValues(gameSettings);
            }
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public boolean isOtherPlayerCommitted() {
            return isOtherPlayerCommitted;
        }

        public boolean isOtherPlayerDecided() {
            return isOtherPlayerDecided;
        }

        public boolean isGameWithCommitment() {
            return isGameWithCommitment;
        }

        public void setIsGameWithCommitment(boolean isGameWithCommitment) {
            this.isGameWithCommitment = isGameWithCommitment;
        }

        public void setIsOtherPlayerDecided(boolean isOtherPlayerDecided) {
            this.isOtherPlayerDecided = isOtherPlayerDecided;
            notifyObservers();
        }

        public void setIsOtherPlayerCommitted(boolean isOtherPlayerCommitted) {
            this.isOtherPlayerCommitted = isOtherPlayerCommitted;
            notifyObservers();
        }

        public ArrayList<MessageHandlerObserver> getObservers() {
            return observers;
        }

        public void setObservers(ArrayList<MessageHandlerObserver> observers) {
            this.observers = observers;
        }

        public void addObserver(MessageHandlerObserver observer) {
            if (!getObservers().contains(observer)) {
                getObservers().add(observer);
            }
        }

        public void removeObserver(MessageHandlerObserver observer) {
            getObservers().remove(observer);
        }

        public void notifyObservers() {
            ArrayList<MessageHandlerObserver> observers = getObservers();
            int size = observers.size();

            for (MessageHandlerObserver observer : observers) {
                observer.update(this);
            }
        }
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    private void displayValues(GameSettings settings) {
        EditText e1 = ((EditText) findViewById(R.id.copCop));
        e1.setText(settings.getCopCop());
        EditText e2 = ((EditText) findViewById(R.id.copDef));
        e2.setText(settings.getCopDef());
        EditText e3 = ((EditText) findViewById(R.id.defCop));
        e3.setText(settings.getDefCop());
        EditText e4 = ((EditText) findViewById(R.id.defDef));
        e4.setText(settings.getDefDef());
        CheckBox checkBox = ((CheckBox) findViewById(R.id.withCommitmentCheckBox));
        checkBox.setChecked(Boolean.valueOf(settings.getWithCommitment()));
        EditText punishmentEditText = (EditText) findViewById(R.id.punishmentEditText);
        punishmentEditText.setText(settings.getPunishment());
        ((Button) findViewById(R.id.settingsSaveButton)).setVisibility(View.GONE);
        findViewById(R.id.settingsExampleTextView).setVisibility(View.GONE);

        if (checkBox.isChecked()) {
            punishmentEditText.setVisibility(View.VISIBLE);
        }

        SharedPreferences sp = getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        String name = sp.getString(Keys.PLAYER_NAME, "DEFAULT");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "DEFAULT");
        String textViewText;

        if (SocketSingleton.getInstance().isHosted()) {
            textViewText = name + " " + surname + " " + "(Player 1)";
        } else {
            textViewText = name + " " + surname + " " + "(Player 2)";
        }

        ((TextView) findViewById(R.id.nameEditText)).setText(textViewText);

        if (!checkBox.isChecked()) {
            findViewById(R.id.commitToLinearLayout).setVisibility(View.GONE);
        } else {
            disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.decideToLinearLayout), false);
        }
    }

    private void disableOrEnableContainerAndChildren(ViewGroup container, boolean enabled) {
        container.setEnabled(enabled);
        int childCount = container.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = container.getChildAt(i);
            child.setEnabled(enabled);

            if (child instanceof ViewGroup) {
                disableOrEnableContainerAndChildren((ViewGroup) child, enabled);
            }
        }
    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();
            String buttonText = ((Button) v).getText().toString();
            GamePlayController gamePlayController = GamePlayController.getInstance();

            DialogFactory dialogFactory = DialogFactory.getInstance();
            dialogFactory.setContext(v.getContext());
            BackgroundJobDialog dialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
            getMessageHandler().addObserver(dialog);
            getMessageHandler().setIsGameWithCommitment(Boolean.valueOf(GameSettings.loadFromPreferences(v.getContext()).getWithCommitment()));

            if (buttonID == R.id.commitToCooperateButton || buttonID == R.id.commitToDefectButton) {
                getMessageHandler().setCurrentState(getMessageHandler().STATE_COMMITMENT);
                getMessageHandler().notifyObservers();

                disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.commitToLinearLayout), false);
                disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.decideToLinearLayout), true);
                gamePlayController.doSaveCommitment(v.getContext(), getParseConnection(), buttonText);
            } else if (buttonID == R.id.decideToCooperateButton || buttonID == R.id.decideToDefectButton) {
                getMessageHandler().setCurrentState(getMessageHandler().STATE_DECISION);
                getMessageHandler().notifyObservers();

                gamePlayController.doSaveDecision(v.getContext(), getParseConnection(), buttonText);
            }
        }
    }

    public ParseConnection getParseConnection() {
        return parseConnection;
    }

    public void setParseConnection(ParseConnection parseConnection) {
        this.parseConnection = parseConnection;
    }
}
