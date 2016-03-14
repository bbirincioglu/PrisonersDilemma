package com.example.bbirincioglu.prisonersdilemma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);
        dialogFactory.create(DialogFactory.DIALOG_PASSWORD).show();
        initialize();
    }

    private void initialize() {
        SettingsController controller = SettingsController.getInstance();
        GameSettings settings = GameSettings.loadFromPreferences(this);
        controller.setGameSettings(settings);

        EditText e1 = ((EditText) findViewById(R.id.copCop));
        e1.setText(settings.getCopCop());
        e1.addTextChangedListener(controller);

        EditText e2 = ((EditText) findViewById(R.id.copDef));
        e2.setText(settings.getCopDef());
        e2.addTextChangedListener(controller);

        EditText e3 = ((EditText) findViewById(R.id.defCop));
        e3.setText(settings.getDefCop());
        e3.addTextChangedListener(controller);

        EditText e4 = ((EditText) findViewById(R.id.defDef));
        e4.setText(settings.getDefDef());
        e4.addTextChangedListener(controller);

        CheckBox checkBox = ((CheckBox) findViewById(R.id.withCommitmentCheckBox));
        checkBox.setChecked(Boolean.valueOf(settings.getWithCommitment()));
        checkBox.setOnClickListener(controller);

        EditText punishmentEditText = (EditText) findViewById(R.id.punishmentEditText);
        punishmentEditText.setText(settings.getPunishment());

        ((Button) findViewById(R.id.settingsSaveButton)).setOnClickListener(controller);

        if (checkBox.isChecked()) {
            punishmentEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
        new ActivitySwitcher().fromPreviousToNext(this, MainMenuActivity.class, bundle, true);
    }
}
