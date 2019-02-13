package com.example.txwu.personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainScreenActivity extends AppCompatActivity {

    private Button stats_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        stats_btn = findViewById(R.id.statsButton);
        stats_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to stats view
            }
        });

        // add fragment from step counter
    }
}
