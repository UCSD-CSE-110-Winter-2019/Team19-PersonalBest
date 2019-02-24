package com.example.team19.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.team19.personalbest.fitness.MainScreen;

public class InitActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Button nextButton;
    private CheckBox termsCheckBox, privacyCheckBox;
    private TextView termsText, privacyText;
    private ScrollView termsLayout, heightLayout;
    private EditText heightInput;
    private Spinner lengthSpinner;
    private PopupWindow pw;

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        prefs = getSharedPreferences("com.exmaple.txwu.personalbest", MODE_PRIVATE);

        // buttons
        termsCheckBox = (CheckBox) findViewById(R.id.termsAndConditionsCheckbox);
        privacyCheckBox = (CheckBox) findViewById(R.id.privacyNoticeCheckbox);
        nextButton = (Button) findViewById(R.id.nextButton);

        // text views
        termsText = findViewById(R.id.termsAndConditions);
        privacyText = findViewById(R.id.privacyNotice);

        // layouts
        termsLayout = findViewById(R.id.terms_privacy_layout);
        heightLayout = findViewById(R.id.height_layout);

        // input
        heightInput = findViewById(R.id.height_input);

        // spinner
        lengthSpinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.length_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lengthSpinner.setAdapter(adapter);

        termsText.setText(R.string.terms_and_conditions);
        privacyText.setText(R.string.privacy_notice);

        nextButton.setEnabled(false);

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(v, R.string.terms_and_conditions_text);
            }
        });

        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(v, R.string.privacy_notice_text);
            }
        });


        termsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextButton();
            }
        });

        privacyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextButton();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    prefs.edit().putBoolean("accepted_terms_and_privacy", true).apply();
                    nextState();
                } else {
                    long height = Long.parseLong(heightInput.getText().toString());
                    prefs.edit().putLong("user_height", height).apply();
                    Cloud.set("Personal Info", "Height", height);

                    String unit = lengthSpinner.getSelectedItem().toString();
                    prefs.edit().putString("user_measurement_unit", unit).apply();
                    Cloud.set("Personal Info", "Measurement Unit", unit);

                    endActivity();
                }
            }
        });

        heightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nextButton.setEnabled(editable.toString().length() > 1);
            }
        });

        if (prefs.getBoolean("accepted_terms_and_privacy", false)) {
            nextState();
            if (prefs.getLong("user_height", 0) != 0) {
                endActivity();
            }
        }
    }

    private void endActivity() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        // https://stackoverflow.com/questions/18957125/how-to-finish-activity-when-starting-other-activity-in-android/18957237
        // cant hit "back" to go back to here
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void nextState() {
        termsLayout.setVisibility(View.GONE);
        heightLayout.setVisibility(View.VISIBLE);
        nextButton.setEnabled(false);
        state = 1;
    }

    private void initiatePopupWindow(View v, int msg) {
        try {
            LayoutInflater inflator = (LayoutInflater) InitActivity.this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflator.inflate(R.layout.popup_terms_privacy,
                    (ViewGroup) findViewById(R.id.popup_element));
            pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pw.showAtLocation(v, Gravity.CENTER, 0, 0);

            TextView pwText = layout.findViewById(R.id.popup_text);
            pwText.setText(msg);
            pwText.setMovementMethod(new ScrollingMovementMethod());

            Button pwButton = layout.findViewById(R.id.popup_button);
            pwButton.setOnClickListener(closePopupWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener closePopupWindow = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    private void updateNextButton() {
        nextButton.setEnabled(state == 0 && termsCheckBox.isChecked() && privacyCheckBox.isChecked());
    }

}
