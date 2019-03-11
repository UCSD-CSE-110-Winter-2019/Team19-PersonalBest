package com.example.team19.personalbest.Friends;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.team19.personalbest.Chat.ChatActivity;
import com.example.team19.personalbest.R;
import com.example.team19.personalbest.fitness.HistoryClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FriendHistoryActivity extends AppCompatActivity {

    private EditText message_text;
    private Button send_btn;
    private BarChart chart;
    private List<BarEntry> entriesStep;
    private HistoryClient history;
    private String user_id;

    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_history);
        user = (Users) getIntent().getSerializableExtra("user");
        user_id = getIntent().getStringExtra("user_id");
        chart = findViewById(R.id.chart);

        history = new FriendHistoryClient(user, System.currentTimeMillis());
        updateChart();

        message_text = findViewById(R.id.history_chat_text);
        send_btn = findViewById(R.id.history_send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    public BarChart getChart() {
        return chart;
    }

    private void updateChart() {

        // get the data from history client
        int[] steps = history.getSteps();

        // enter the data into the bar chart
        entriesStep = new ArrayList<>();

        for (int i = 0; i < steps.length; i++) {
            entriesStep.add(new BarEntry(i, steps[i]));
        }

        BarDataSet stepSet = new BarDataSet(entriesStep, "Steps");

        // Configure the bar chart
        stepSet.setColors(Color.BLUE);

        // the labels that should be drawn on the XAxis
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long millis = history.getFirstDayTimeMillis();
                millis += (int)value * history.MILLIS_A_DAY;
                return new SimpleDateFormat("MM-dd", Locale.US).format(new Date(millis));
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(formatter);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(28);

        chart.setVisibleXRangeMaximum(28);
        chart.setVisibleXRangeMinimum(28);

        BarData data = new BarData(stepSet);
        data.setBarWidth(0.9f);
        // data.setValueFormatter(new MyValueFormatter);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate(); // refresh the chart
    }

    /**
     * Setter of history client
     * @param history - the history client used to make chart
     */
    public void setHistoryClient(HistoryClient history) {
        this.history = history;
        updateChart();
    }

    private void sendMessage() {
        String text = message_text.getText().toString();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_email", user.getEmail());
        intent.putExtra("user_id", user_id);
        intent.putExtra("input_text", text);
        startActivity(intent);
        message_text.setText("");
    }

}

class FriendHistoryClient extends HistoryClient {

    private Users user;
    private Map<String, Integer> steps;
    private Date[] dateOfMonth;
    private long currentTimeMillis;

    public FriendHistoryClient(Users user, long currentTimeMillis) {
        super();
        if (user != null) {
            this.user = user;
            this.steps = user.getSteps();
        }
        this.currentTimeMillis = currentTimeMillis;

        dateOfMonth = new Date[28];
        long timeDif = 0;
        for (int i = 27; i >= 0; i--) {
            dateOfMonth[i] = new Date(currentTimeMillis - timeDif);
            timeDif += MILLIS_A_DAY;
        }
    }

    @Override
    public String[] getDateFormat() {
        String[] dateFormat = new String[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            dateFormat[i] = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(dateOfMonth[i]);
        }

        return dateFormat;
    }

    @Override
    public int[] getSteps() {

        String[] dates = getDateFormat();
        int[] steps = new int[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            if (this.steps != null)
                steps[i] = this.steps.get(dates[i]) == null ? 0 : this.steps.get(dates[i]);
            else
                steps[i] = 0;
        }

        return steps;
    }

    @Override
    public long getFirstDayTimeMillis() {
        return currentTimeMillis - 27 * MILLIS_A_DAY;
    }
}