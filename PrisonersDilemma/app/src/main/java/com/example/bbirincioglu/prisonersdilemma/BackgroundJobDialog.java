package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * This class is responsible from displaying a dialog with appropriate message when there is a backend job going on. It is a part of Observer Design Pattern.
 * It is observer because it is a GUI object. When there is a backend job related to database for example, it listens related domain object (an instance of
 * ParseConnection in this case) by implemeting ParseConnectionObserver. When there is a bluetooth backend processing, it listens MessageHandler object
 * which is a subclass of GamePlayActivity by implemeting GamePlayActivity.MessageHandlerObserver etc.
 */
public class BackgroundJobDialog extends Dialog implements SimpleDialog, ParseConnectionObserver, ConnectionThreadObserver, GamePlayActivity.MessageHandlerObserver, WriterObserver {
    private Activity activity;

    public BackgroundJobDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog); // Dialog style
        setActivity((Activity) context); // activity on which dialog is displayed.
        setTitle("BACKGROUND JOB");
        setCancelable(false); // By doing this, we prevent user to close this activity by touching anywhere on the screen. He will not be able to close it.

        /*Next 3 if clauses are there, whenever we create a dialog, we add it into activities' dialog array list so that when someone switch from one activity
        to another, we can close the dialogs first, and close the activity.*/
        if (getActivity() instanceof BluetoothGameActivity) {
            ((BluetoothGameActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GamePlayActivity) {
            ((GamePlayActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GameResultsActivity) {
            ((GameResultsActivity) getActivity()).getDialogs().add(this);
        }
    }

    @Override
    public void initialize() {
        setContentView(R.layout.background_job_dialog); //GUI objects contained by this dialog are defined in the xml. In other words layout of this dialogs are
        //defined in the xml files.
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /*
       When there is a change in the parseConnection object, it notifies all the observers by calling the method below. It gives itself as the argument
       to the method so that GUI objects understand in which state parseConnection object is, and update their appearance accordingly.
    */
    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {  //Do nothing as there is no database connection work.

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {  //Open this dialog as there is some work started.
            show();
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) { //Close this dialog as work has finished.
            cancel();
        }
    }

    /*
        The method below observers connectionThread object. When two player tries to build a bluetooth connection, hosted player's phone (ServerConnectionThread)
        constantly listens for the other player to join (ClientConnectionThread). Don't forget that ConnectionThread is actually an interface implemented by
        ServerConnectionThread and ClientConnectionThread.
     */
    @Override
    public void update(ConnectionThread connectionThread) {
        ServerConnectionThread serverConnectionThread;
        ClientConnectionThread clientConnectionThread;
        int currentStatus;
        TextView textView = ((TextView) findViewById(R.id.backgroundJobTextView));

        if (connectionThread instanceof ServerConnectionThread) {  //This means it is hosted player.
            serverConnectionThread = (ServerConnectionThread) connectionThread;
            currentStatus = serverConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME) { //This means there is no one joining the game. Thus, show the dialog.
                textView.setText("Waiting For Someone To Join The Game");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_SOMEONE_JOINED_GAME) {  // Someone joined the game, change the dialog content, put "Start Game" button.
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) { //If connection failed, make dialog cancelable so that user can cancel the dialog and restart all steps.
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        } else if (connectionThread instanceof ClientConnectionThread) {
            clientConnectionThread = (ClientConnectionThread) connectionThread;
            currentStatus = clientConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_CONNECTING) { //If client tries to connect in the backend, show dialog with suitable message.
                textView.setText("Connecting...");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTED) { //If client is connected, put "Start Game" button to initialize the game.
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) { // If connection is failed, make dialog cancelable so that user can retry to connect.
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        }
    }

    //This method is called during the actual game play. We display dialogs with various message to make one player wait, while other is commiting so something
    //or when the game turn is others.
    @Override
    public void update(GamePlayActivity.MessageHandler messageHandler) {
        if (!isShowing()) {
            findViewById(R.id.startGameButton).setVisibility(View.GONE);
            show();
        }

        String waitingCommit = "Waiting For Other Player To Commit...";
        String waitingDecide = "Waiting For Other Player To Decide...";

        boolean isGameWithCommitment = messageHandler.isGameWithCommitment();
        boolean isOtherPlayerCommitted = messageHandler.isOtherPlayerCommitted();
        boolean isOtherPlayerDecided = messageHandler.isOtherPlayerDecided();
        TextView textView = (TextView) findViewById(R.id.backgroundJobTextView);
        GamePlayActivity gamePlayActivity = ((GamePlayActivity) getActivity());

        if (isGameWithCommitment) { //game is played with commitment.
            if (messageHandler.getCurrentState().equals(GamePlayActivity.MessageHandler.STATE_COMMITMENT)) { //your state is commitment
                if (isOtherPlayerCommitted) { //other has already committed, we have to close the dialog as you need to commit also.
                    dismiss();
                } else {
                    textView.setText(waitingCommit); //Otherwise, we have to display a dialog for you in order to make you wait for other's commitment.
                }
            } else if (messageHandler.getCurrentState().equals(GamePlayActivity.MessageHandler.STATE_DECISION)) { //your state is decision.
                if (isOtherPlayerDecided && ((GamePlayActivity) getActivity()).getParseConnection().isMyDecisionSaved()) { //other is decided already, and your decision is also saved, so we need to close the dialogs.
                    dismiss();
                    GamePlayController controller = gamePlayActivity.getGamePlayController();
                    ParseConnection parseConnection = gamePlayActivity.getParseConnection();
                    controller.doDisplayGameResult(gamePlayActivity, parseConnection);
                } else { //otherwise, either other player is not decided yet, or your decision is not saved in the server, so we need to display a waiting dialog.
                    textView.setText(waitingDecide);
                }
            }
        } else { //game is not played with commitment.
            if (isOtherPlayerDecided && gamePlayActivity.getParseConnection().isMyDecisionSaved()) { //other player is already decided, and your decision is also saved, thus we have to close game is over.
                dismiss();
                GamePlayController controller = gamePlayActivity.getGamePlayController();
                ParseConnection parseConnection = gamePlayActivity.getParseConnection();
                controller.doDisplayGameResult(getActivity(), parseConnection);
            } else {
                textView.setText(waitingDecide); //either other not decided yet, or your decision is not saved yet. Thus, display a waiting dialog.
            }
        }
    }

    /*
        This method below is when we extract all the game results into excel file by using instance of Writer class.
     */
    @Override
    public void update(Writer writer) {
        int currentState = writer.getCurrentState();
        TextView textView = (TextView) findViewById(R.id.backgroundJobTextView);

        System.out.println("CURRENT STATE OF WRITER: " + currentState);

        if (currentState == Writer.STATE_NO_WRITING) {
            if (isShowing()) {
                dismiss();
            }
        } else if (currentState == Writer.STATE_WRITING) {
            textView.setText("Writing Into Secure Digital Card with File Name: \"gameResults.xls\"");
            System.out.println(textView.getText());
            show();
        } else if (currentState == Writer.STATE_WRITING_FAILED) {
            textView.setText("Writing Failed: " + writer.getError());
            show();
        }
    }

    //Basic button listener which is binded to "Start Game" button. When this button is clicked, we switch from the current activity(BluetoothGameActivity) to
    // GamePlayActivity.
    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            new ActivitySwitcher().fromPreviousToNext(getActivity(), GamePlayActivity.class, null, true);
        }
    }
}
