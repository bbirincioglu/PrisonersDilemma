package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by bbirincioglu on 3/16/2016.
 */
public class GameResultDialog extends Dialog implements SimpleDialog {
    private Activity activity;

    public GameResultDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(false);
        setActivity((Activity) context);

        if (getActivity() instanceof BluetoothGameActivity) {
            ((BluetoothGameActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GamePlayActivity) {
            ((GamePlayActivity) getActivity()).getDialogs().add(this);
        }
    }

    @Override
    public void initialize() {
        setContentView(R.layout.game_result_dialog);
        ButtonListener buttonListener = new ButtonListener();
        findViewById(R.id.restartButton).setOnClickListener(buttonListener);
        findViewById(R.id.exitButton).setOnClickListener(buttonListener);
    }

    public void injectContent(GameResult gameResult) {
        displayValues(GameSettings.loadFromPreferences(getActivity()));
        GameResultEvaluator evaluator = new GameResultEvaluator();
        int[] result = evaluator.evaluate(gameResult);

        gameResult.setP1Payoff(String.valueOf(result[0]));
        gameResult.setP2Payoff(String.valueOf(result[1]));

        String p1Name = gameResult.getP1Name();
        String p1Surname = gameResult.getP1Surname();
        String p1Commitment = gameResult.getP1Commitment();
        String p1Decision = gameResult.getP1Decision();
        String p1Payoff = String.valueOf(result[0]);

        String p2Name = gameResult.getP2Name();
        String p2Surname = gameResult.getP2Surname();
        String p2Commitment = gameResult.getP2Commitment();
        String p2Decision = gameResult.getP2Decision();
        String p2Payoff = String.valueOf(result[1]);

        ((TextView) findViewById(R.id.p1NameEditText)).setText(p1Name);
        ((TextView) findViewById(R.id.p1CommitmentEditText)).setText(p1Commitment);
        ((TextView) findViewById(R.id.p1DecisionEditText)).setText(p1Decision);
        ((TextView) findViewById(R.id.p1PayoffEditText)).setText(p1Payoff);

        ((TextView) findViewById(R.id.p2NameEditText)).setText(p2Name);
        ((TextView) findViewById(R.id.p2CommitmentEditText)).setText(p2Commitment);
        ((TextView) findViewById(R.id.p2DecisionEditText)).setText(p2Decision);
        ((TextView) findViewById(R.id.p2PayoffEditText)).setText(p2Payoff);
    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();

            if (buttonID == R.id.restartButton) {
                new ActivitySwitcher().fromPreviousToNext(getActivity(), BluetoothGameActivity.class, null, true);
            } else if (buttonID == R.id.exitButton) {
                try {
                    SocketSingleton.getInstance().getSocket().close();
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

        disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.settingsLinearLayout), false);
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
}
