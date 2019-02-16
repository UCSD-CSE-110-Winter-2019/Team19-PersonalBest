package com.example.txwu.personalbest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.txwu.personalbest.fitness.HistoryClient;
import com.example.txwu.personalbest.fitness.MainScreen;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        int[] goals = history.getGoalsForWeek();
        int[] intentionals = history.getIntentional();
        int[] incidentals = history.getIncidentals();

        entriesGoal = new ArrayList<>();
        entriesStep = new ArrayList<>();

        for (int i = 0; i < goals.length; i++) {
            entriesGoal.add(new BarEntry(i, goals[i]));
            entriesStep.add(new BarEntry(i, new float[] {incidentals[i], intentionals[i]}));
        }
        // add data to entries
        // BarEntry stackedEntry = new BarEntry(0f, new float[] { 10, 20, 30 });
        // public BarEntry(float x, float [] yValues) { ... }
        // https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data#stacked-barchart

        BarDataSet goalSet = new BarDataSet(entriesGoal, "Goal");
        BarDataSet stepSet = new BarDataSet(entriesStep, "Steps");
        stepSet.setStackLabels(new String[] {"Incidental", "Intentional"});

        goalSet.setColor(Color.RED);
        stepSet.setColors(Color.GREEN, Color.BLUE);
        //set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //set.setDrawIcons(false);
        //set.setColors();
        //set.setStackLabels(new String[]{"Intentional", "Incidental"});
        //ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        //dataSets.add(set);


        // the labels that should be drawn on the XAxis
        final String[] quarters = history.getDateFormat(); // etc the dates for the x axis

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setValueFormatter(formatter);

        BarData data = new BarData(stepSet, goalSet);
        data.setBarWidth(0.5f);
        // data.setValueFormatter(new MyValueFormatter);
        data.setValueTextColor(Color.BLACK);

        float groupSpace = 0.06f;
        float barSpace = 0f;

        chart.setData(data);
        chart.setFitBars(true);
        chart.groupBars(-1f, groupSpace, barSpace);
        chart.invalidate(); // refresh the chart
    }
}
