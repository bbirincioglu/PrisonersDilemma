package com.example.bbirincioglu.prisonersdilemma;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parse.Parse;
import com.parse.ParseObject;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePlayActivity extends AppCompatActivity {
    private MessageHandler messageHandler; //For receiving messages from ConnectedThread object (backend thread), and to send message to main thread.
    private ParseConnection parseConnection; //For connecting Parse data base.
    private GamePlayController gamePlayController; //For controlling this activity. (Getting user inputs such as button clicks, text enters etc. and sending to domain objects for processing.)
    private ArrayList<Dialog> dialogs;  //Dialogs currently opened.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setDialogs(new ArrayList<Dialog>());

        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);

        setMessageHandler(new MessageHandler(this));
        SocketSingleton socketSingleton = SocketSingleton.getInstance(); //Singleton which stores whether player is hosted or client, and bluetooth socket.

        ParseObject.registerSubclass(GameResult.class); //register new sub class (sub class of ParseObject) to Parse database. This is required for introducing your new sub class to database for recognition.
        setParseConnection(ParseConnection.getNewInstance()); //First I tried to implement ParseConnection via singleton. It didn't work, thus I created a method which returns new instance each time it is called.

        setGamePlayController(new GamePlayController());
        GamePlayController gamePlayController = getGamePlayController();
        gamePlayController.setConnectedThread(new ConnectedThread(this, socketSingleton.getSocket(), getMessageHandler())); //socket is argument because its inputStream, and outputStream will be used for reading
                                                                                                                            //and writing and messageHandler is argument as it provides communication between
                                                                                                                            //connectedThread and main thread.
        gamePlayController.getConnectedThread().start(); //Start connected thread in order to communicate with the other player.

        getParseConnection().setGamePlayController(gamePlayController);

        BackgroundJobDialog connectServerDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        connectServerDialog.setContentView(composeInformativeContentView("Waiting For Server And Opponent Response..."));
        getParseConnection().addObserver(connectServerDialog); //Bind Observer (connectServerDialog) to observable (parseConnection)

        if (socketSingleton.isHosted()) {
            System.out.println("IS HOSTED.");
            GameSettings gameSettings = GameSettings.loadFromPreferences(this); //Read game settings from android's built-in Preferences.
            gamePlayController.doWrite(gameSettings.toHashMap()); //If hosted player, then send Game Settings to client player.
            displayValues(gameSettings); //display game settings on the screen.

            getParseConnection().createEmptyGameResult(); // go to the database and create an empty row which will be filled during the game play.
        } else {
            getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_STARTED); //wait for hosted player to send Game Settings, and successful connection to parse database.
            System.out.println("IS NOT HOSTED.");
        }

        disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.settingsLinearLayout), false); //disable all the game settings view so that they can't be manipulated by players.
        attachListeners(); //attach any GUI object to their corresponding listeners.
    }

    private void attachListeners() {
        ButtonListener buttonListener = new ButtonListener();
        findViewById(R.id.commitToCooperateButton).setOnClickListener(buttonListener);
        findViewById(R.id.commitToDefectButton).setOnClickListener(buttonListener);
        findViewById(R.id.decideToCooperateButton).setOnClickListener(buttonListener);
        findViewById(R.id.decideToDefectButton).setOnClickListener(buttonListener);
    }

    private LinearLayout composeInformativeContentView(String message) {
        TextView informativeTextView = new TextView(this);
        informativeTextView.setText(message);
        informativeTextView.setGravity(Gravity.CENTER);
        informativeTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout dummyContainer = new LinearLayout(this);
        dummyContainer.setGravity(Gravity.CENTER);
        dummyContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) dummyContainer.getLayoutParams()).gravity = Gravity.CENTER;
        dummyContainer.addView(informativeTextView);
        return dummyContainer;
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

        //Receive the message coming from backend thread (ConnectedThread object), and convert that message into original format such as String (normal message) or HashMap (GameSettings).
        @Override
        public void handleMessage(Message msg) {
            String messageAsString = null;
            HashMap<String, String> messageAsGameSettings = null;

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
                ObjectInputStream ois = new ObjectInputStream(bais);
                messageAsGameSettings = (HashMap<String, String>) ois.readObject(); //try to convert HashMap, if it gives an exception, that means object is of type String.
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
                    messageAsString = new String((byte[]) msg.obj, 0, msg.arg1); //then convert it to String.
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            //The if clauses below are the indicators for which message has been sent by the other player. If it message contains "gameNoOnly", this means message is game number. If this is 5. game for example
            //hosted player sends something like "gameNoOnly:5". Finally Split message into 2 "gameNoOnly:", and "5" to get game number of current game. This is required to access specific row in the database.
            //I consider game number as the primary key for the table in the database.
            if (messageAsString != null) { //Message is of type String
                if (messageAsString.contains("gameNoOnly:")) {
                    System.out.println("IN THE GAME NO ONLY.");
                    System.out.println("split 1: " + messageAsString.split(":")[0] + "split 2: " + messageAsString.split(":")[1]);
                    getParseConnection().setCurrentGameNo(Integer.valueOf(messageAsString.split(":")[1]));
                    getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_FINISHED);
                    System.out.println("IN THE GAME NO ONLY: " + getParseConnection().getCurrentGameNo());
                } else if (messageAsString.equals("committed")){ //Other player committed, and notified us by sending "committed" string.
                    setIsOtherPlayerCommitted(true);
                } else if (messageAsString.equals("decided")) { //Other player decided, and notified us by sending "decided" string.
                    setIsOtherPlayerDecided(true);
                }
            } else if (messageAsGameSettings != null) { //Message is of Type Object (Hash Map)
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

    //Displays all the game settings on the game play screen.
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

        if (SocketSingleton.getInstance().isHosted()) { //Hosted player is labeled as "Player 1".
            textViewText = name + " " + surname + " " + "(Player 1)";
        } else {
            textViewText = name + " " + surname + " " + "(Player 2)";
        }

        ((TextView) findViewById(R.id.nameSurnameEditText)).setText(textViewText);

        if (!checkBox.isChecked()) {
            findViewById(R.id.commitToLinearLayout).setVisibility(View.GONE);
        } else {
            disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.decideToLinearLayout), false);
        }
    }

    //To enable (clickable) or disable (unclickable) a GUI object.
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

    //Button listener for commitment and decision buttons.
    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();
            String buttonText = ((Button) v).getText().toString();
            GamePlayController gamePlayController = getGamePlayController();

            DialogFactory dialogFactory = DialogFactory.getInstance();
            dialogFactory.setContext(v.getContext());
            BackgroundJobDialog dialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
            getMessageHandler().addObserver(dialog);
            getMessageHandler().setIsGameWithCommitment(Boolean.valueOf(GameSettings.loadFromPreferences(v.getContext()).getWithCommitment()));

            if (buttonID == R.id.commitToCooperateButton || buttonID == R.id.commitToDefectButton) { //You clicked one of the commitment buttons.
                getMessageHandler().setCurrentState(getMessageHandler().STATE_COMMITMENT); //change message handler state as commitment is done.
                getMessageHandler().notifyObservers(); //notify any GUI object (dialogs) which "observers" message handler.

                disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.commitToLinearLayout), false); //make commitment buttons disabled so that they can't be clicked again.
                disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.decideToLinearLayout), true); //make decision buttons enabled so that user can decide at the end.
                gamePlayController.doSaveCommitment(v.getContext(), getParseConnection(), buttonText); //save commitment result of player to the server.
            } else if (buttonID == R.id.decideToCooperateButton || buttonID == R.id.decideToDefectButton) { //You clicked one of the decision buttons.
                getMessageHandler().setCurrentState(getMessageHandler().STATE_DECISION); //change message handler state as decision is done.
                getMessageHandler().notifyObservers(); //notify any GUI object (dialogs) that listens message handler.
                gamePlayController.doSaveDecision(v.getContext(), getParseConnection(), buttonText);  // save decision to the server.
            }
        }
    }

    public ParseConnection getParseConnection() {
        return parseConnection;
    }

    public void setParseConnection(ParseConnection parseConnection) {
        this.parseConnection = parseConnection;
    }

    public GamePlayController getGamePlayController() {
        return gamePlayController;
    }

    public void setGamePlayController(GamePlayController gamePlayController) {
        this.gamePlayController = gamePlayController;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            SocketSingleton.getInstance().getSocket().close(); //close the socket in case the activity is destroyed.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
