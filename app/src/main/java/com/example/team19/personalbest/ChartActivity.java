package com.example.team19.personalbest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.team19.personalbest.fitness.HistoryClient;
import com.example.team19.personalbest.fitness.MainScreen;
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

public class ChartActivity extends AppCompatActivity {

    private BarChart chart;
    private List<BarEntry> entriesGoal;
    private List<BarEntry> entriesStep;
    private HistoryClient history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.chart);

        history = new HistoryClient(System.currentTimeMillis() + MainScreen.timedif, this);
        updateChart();
    }

    private void updateChart() {

        // get the data from history client
        int[] goals = history.getGoals();
        int[] intentionals = history.getIntentional();
        int[] incidentals = history.getIncidentals();

        // enter the data into the bar chart
        entriesGoal = new ArrayList<>();
        entriesStep = new ArrayList<>();

        for (int i = 0; i < goals.length; i++) {
            entriesGoal.add(new BarEntry(i, goals[i]));
            entriesStep.add(new BarEntry(i, new float[] {incidentals[i], intentionals[i]}));
        }

        BarDataSet goalSet = new BarDataSet(entriesGoal, "Goal");
        BarDataSet stepSet = new BarDataSet(entriesStep, "Steps");
        stepSet.setStackLabels(new String[] {"Incidental", "Intentional"});

        // Configure the bar chart
        goalSet.setColor(Color.RED);
        stepSet.setColors(Color.BLUE, Color.GREEN);


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

        chart.setVisibleXRangeMaximum(7);
        chart.setVisibleXRangeMinimum(7);
        chart.moveViewToX(22);

        BarData data = new BarData(stepSet, goalSet);
        data.setBarWidth(0.45f);
        // data.setValueFormatter(new MyValueFormatter);
        data.setValueTextColor(Color.BLACK);

        float groupSpace = 0.1f;
        float barSpace = 0f;

        chart.setData(data);
        chart.setFitBars(true);
        chart.groupBars(0f, groupSpace, barSpace);
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

    /**
     * getter of chart
     * @return - bar chart
     */
    public BarChart getChart() {
        return chart;
    }
}
