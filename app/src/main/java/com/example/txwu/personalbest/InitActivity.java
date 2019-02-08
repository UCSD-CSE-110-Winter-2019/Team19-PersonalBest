package com.example.txwu.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class InitActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Button nextButton;
    private CheckBox termsCheckBox, privacyCheckBox;
    private TextView termsText, privacyText;
    private PopupWindow pw;

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
                prefs.edit().putBoolean("accepted_terms_and_privacy", true).apply();

                finish();
            }
        });
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
        nextButton.setEnabled(termsCheckBox.isChecked() && privacyCheckBox.isChecked());
    }

}
