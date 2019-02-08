package com.example.txwu.personalbest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class InitActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Button nextButton;
    private CheckBox termsCheckBox, privacyCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        prefs = getSharedPreferences("com.exmaple.txwu.personalbest", MODE_PRIVATE);

        termsCheckBox = (CheckBox) findViewById(R.id.termsAndConditionsCheckbox);
        privacyCheckBox = (CheckBox) findViewById(R.id.privacyNoticeCheckbox);
        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setEnabled(false);

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
                prefs.edit().putBoolean("accepted_terms_and_privacy", true).apply();

                finish();
            }
        });
    }

    private void updateNextButton() {
        nextButton.setEnabled(termsCheckBox.isChecked() && privacyCheckBox.isChecked());
    }

}
