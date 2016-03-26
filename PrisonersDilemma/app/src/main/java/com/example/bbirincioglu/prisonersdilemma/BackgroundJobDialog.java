package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class BackgroundJobDialog extends Dialog implements SimpleDialog, ParseConnectionObserver, ConnectionThreadObserver, GamePlayActivity.MessageHandlerObserver, WriterObserver {
    private Activity activity;

    public BackgroundJobDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setActivity((Activity) context);
        setTitle("BACKGROUND JOB");
        setCancelable(false);

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
        setContentView(R.layout.background_job_dialog);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {
            show();
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) {
            cancel();
        }
    }

    @Override
    public void update(ConnectionThread connectionThread) {
        ServerConnectionThread serverConnectionThread;
        ClientConnectionThread clientConnectionThread;
        int currentStatus;
        TextView textView = ((TextView) findViewById(R.id.backgroundJobTextView));

        if (connectionThread instanceof ServerConnectionThread) {
            serverConnectionThread = (ServerConnectionThread) connectionThread;
            currentStatus = serverConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME) {
                textView.setText("Waiting For Someone To Join The Game");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_SOMEONE_JOINED_GAME) {
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) {
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        } else if (connectionThread instanceof ClientConnectionThread) {
            clientConnectionThread = (ClientConnectionThread) connectionThread;
            currentStatus = clientConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_CONNECTING) {
                textView.setText("Connecting...");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTED) {
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) {
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        }
    }

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

        if (isGameWithCommitment) {
            if (messageHandler.getCurrentState().equals(GamePlayActivity.MessageHandler.STATE_COMMITMENT)) {
                if (isOtherPlayerCommitted) {
                    dismiss();
                } else {
                    textView.setText(waitingCommit);
                }
            } else if (messageHandler.getCurrentState().equals(GamePlayActivity.MessageHandler.STATE_DECISION)) {
                if (isOtherPlayerDecided && ((GamePlayActivity) getActivity()).getParseConnection().isMyDecisionSaved()) {
                    dismiss();
                    GamePlayController controller = gamePlayActivity.getGamePlayController();
                    ParseConnection parseConnection = gamePlayActivity.getParseConnection();
                    controller.doDisplayGameResult(gamePlayActivity, parseConnection);
                } else {
                    textView.setText(waitingDecide);
                }
            }
        } else {
            if (isOtherPlayerDecided && gamePlayActivity.getParseConnection().isMyDecisionSaved()) {
                dismiss();
                GamePlayController controller = gamePlayActivity.getGamePlayController();
                ParseConnection parseConnection = gamePlayActivity.getParseConnection();
                controller.doDisplayGameResult(getActivity(), parseConnection);
            } else {
                textView.setText(waitingDecide);
            }
        }
    }

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

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            new ActivitySwitcher().fromPreviousToNext(getActivity(), GamePlayActivity.class, null, true);
        }
    }
}
