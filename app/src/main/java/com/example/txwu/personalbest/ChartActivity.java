package com.example.txwu.personalbest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private BarChart chart;
    private List<BarEntry> entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.chart);

        // updateChart();
    }

    private void updateChart() {
        entries = new ArrayList<>();
        // add data to entries
        // BarEntry stackedEntry = new BarEntry(0f, new float[] { 10, 20, 30 });
        // public BarEntry(float x, float [] yValues) { ... }
        // https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data#stacked-barchart

        BarDataSet set = new BarDataSet(entries, , "Weekly Statistics");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawIcons(false);
        set.setColors();
        set.setStackLabels(new String[]{"Intentional", "Incidental"});
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);


        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[] { "4-14-2019" }; // etc the dates for the x axis

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        BarData data = new BarData(dataSets);
        // data.setValueFormatter(new MyValueFormatter);
        //data.setValueTextColor(Color.WHITE);
        data.setBarWidth(0.5f);
        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate(); // refresh the chart
    }
}
