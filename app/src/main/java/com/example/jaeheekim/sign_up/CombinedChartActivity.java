package com.example.jaeheekim.sign_up;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
public class CombinedChartActivity extends AppCompatActivity {

    private CombinedChart mChart;
    int AQI[] = {35, 58, 124, 166, 260, 380};
    int CO2[] = {35, 20, 2, 166, 255, 380};
    int O3[] = {20, 58, 124, 40, 140, 211};
    int NO2[] = {34, 14, 42, 98, 260, 300};
    TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_chart);

        // get sensor location
        Intent intent = getIntent();
        String location = intent.getStringExtra("Location");
        locationView = findViewById(R.id.location);
        locationView.setText(location + "'s Air condition");

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.getDescription().setText("Current Air Condition");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(true);
        mChart.setHighlightFullBarEnabled(true);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });
        mChart.setDoubleTapToZoomEnabled(true);

        CombinedData data = new CombinedData();

        float groupSpace = 0.04f;
        float barSpace = 0.0f;
        float barWidth = 0.28f;

        // group bar chart
        BarData barData = new BarData(generateBarData()
                , generateBarData2(), generateBarData3());

        barData.setBarWidth(barWidth);

        // combine bar and line
        data.setData(generateLineData());
        data.setData(barData);
        data.setDrawValues(true);
        barData.groupBars(0, groupSpace, barSpace);

        // setting
        mChart.setData(data);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(0 +
                mChart.getBarData().getGroupWidth(groupSpace, 0) * 6);

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mDay[(int) value % mDay.length];
            }
        });
        mChart.animateY(1000);
        mChart.invalidate();
    }

    protected String[] mDay = new String[]{
            "28/07", "29/07", "30/07", "31/08", "01/08", "02/08"
    };

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, AQI[i]));
        }

        LineDataSet set = new LineDataSet(entries, "AQI");
        set.setColor(Color.rgb(0, 0, 255));
        set.setLineWidth(4.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarDataSet generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, CO2[i]));
        }

        BarDataSet set = new BarDataSet(entries, "CO2");
        set.setColor(Color.rgb(250, 120, 28));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set;
    }

    private BarDataSet generateBarData2() {

        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 6; i++) {
            entries2.add(new BarEntry(i, O3[i]));
        }

        BarDataSet set2 = new BarDataSet(entries2, "O3");
        set2.setColor(Color.rgb(0, 250, 250));
        set2.setValueTextColor(Color.rgb(50, 210, 98));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set2;
    }

    private BarDataSet generateBarData3() {

        ArrayList<BarEntry> entries3 = new ArrayList<BarEntry>();

        for (int i = 0; i < 6; i++) {
            entries3.add(new BarEntry(i, NO2[i]));
        }

        BarDataSet set3 = new BarDataSet(entries3, "NO2");
        set3.setColor(Color.rgb(50, 150, 10));
        set3.setValueTextColor(Color.rgb(50, 210, 98));
        set3.setValueTextSize(10f);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set3;
    }
}
