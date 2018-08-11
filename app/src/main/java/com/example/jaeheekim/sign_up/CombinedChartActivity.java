package com.example.jaeheekim.sign_up;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
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
    int AQI[] = {35, 58, 124, 166, 260, 380, 225};
    int CO[] = {35, 20, 2, 166, 255, 380, 72};
    int O3[] = {20, 58, 124, 40, 140, 211, 152};
    int NO2[] = {34, 14, 42, 98, 260, 300, 112};
    int SO2[] = {8, 14, 60, 44, 120, 20, 225};
    TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined_chart);

        mChart = findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(true);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1.0f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mDay[(int) value % mDay.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.animateY(1000);
        mChart.invalidate();
    }

    protected String[] mDay = new String[]{
            "28/07", "29/07", "30/07", "31/08", "01/08", "02/08", "03/08"
    };

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i+0.45f, AQI[i]));
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

    private BarData generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries4 = new ArrayList<BarEntry>();

        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, CO[i]));
            entries2.add(new BarEntry(i, O3[i]));
            entries3.add(new BarEntry(i, NO2[i]));
            entries4.add(new BarEntry(i, SO2[i]));
        }

        BarDataSet set = new BarDataSet(entries, "CO");
        set.setColor(Color.rgb(250, 120, 28));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "O3");
        set2.setColor(Color.rgb(0, 250, 250));
        set2.setValueTextColor(Color.rgb(50, 210, 98));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set3 = new BarDataSet(entries3, "NO2");
        set3.setColor(Color.rgb(50, 150, 10));
        set3.setValueTextColor(Color.rgb(50, 210, 98));
        set3.setValueTextSize(10f);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set4 = new BarDataSet(entries4, "SO2");
        set4.setColor(Color.rgb(128, 120, 120));
        set4.setValueTextColor(Color.rgb(60, 0, 255));
        set4.setValueTextSize(10f);
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.025f;
        float barSpace = 0.0f; // x2 dataset
        float barWidth = 0.232f; // x2 dataset

        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set, set2, set3, set4);
        d.setBarWidth(barWidth);

        d.groupBars(0, groupSpace, barSpace);

        return d;
    }
}