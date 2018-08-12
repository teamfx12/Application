package com.example.jaeheekim.sign_up;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CombinedChartActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private CombinedChart mChart;
    int AQI[] = {35, 58, 124, 166, 260, 380, 225};
    int CO[] = {35, 20, 2, 166, 255, 380, 72};
    int O3[] = {20, 58, 124, 40, 140, 211, 152};
    int NO2[] = {34, 14, 42, 98, 260, 300, 112};
    int SO2[] = {8, 14, 60, 44, 120, 20, 225};

    TextView location;
    private Spinner periodSpinner;
    private Spinner pollutantSpinner;
    List<String> listPeriod;
    List<String> listPollutant;
    ArrayAdapter<String> perSpinnerAdapter;
    int period = 1;
    String pollutant = "All";
    ArrayAdapter<String> polluSpinnerAdapter;
    private String id;

    int listSize;

    private static ArrayList<String> AQIArray = new ArrayList<String>();
    private static ArrayList<String> COArray = new ArrayList<String>();
    private static ArrayList<String> O3Array = new ArrayList<String>();
    private static ArrayList<String> NO2Array = new ArrayList<String>();
    private static ArrayList<String> SO2Array = new ArrayList<String>();
    private static ArrayList<String> xValue = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined_chart);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");

        location = (TextView) findViewById(R.id.location);
        location.setText(name.split(" : ")[1].replace(" ","")+"'s Historical Chart");

        mChart = findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(true);
        mChart.setHighlightFullBarEnabled(false);

        periodSpinner = (Spinner) findViewById(R.id.period);

        listPeriod = new ArrayList<String>(); // List of Items
        listPeriod.add("A day");
        listPeriod.add("A week");
        listPeriod.add("A month");

        perSpinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, listPeriod){
            //By using this method we will define how
            // the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }
            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#E30D81"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }
        };
        perSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        // Set Adapter in the spinner
        periodSpinner.setAdapter(perSpinnerAdapter);

        pollutantSpinner = (Spinner) findViewById(R.id.pollutant);

        listPollutant = new ArrayList<String>(); // List of Items
        listPollutant.add("All");
        listPollutant.add("AQI");
        listPollutant.add("CO");
        listPollutant.add("O3");
        listPollutant.add("SO2");
        listPollutant.add("NO2");

        polluSpinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, listPollutant){
            //By using this method we will define how
            // the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }
            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#E30D81"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }
        };
        polluSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        // Set Adapter in the spinner
        pollutantSpinner.setAdapter(polluSpinnerAdapter);

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String temperiod = adapterView.getItemAtPosition(i).toString();
            if(temperiod.equals("A day")){
                period = 0;
            } else if(temperiod.equals("A week")){
                period = 1;
            } else{
                period = 2;
            }
        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                period = 1;
            }
        });


        pollutantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pollutant = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                pollutant = "All";
            }
        });

         mChart.invalidate();
    }
    public void onClickSubmit(View view) {

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
                return xValue.get((int) value % xValue.size());
            }
        });
        if (GlobalVar.getFlag()) {
            GlobalVar.setFlag(false);

            String url = "http://teamf-iot.calit2.net/API/view";
            String msg = "function=view_historical_air&token=" + GlobalVar.getToken()
                    + "&id=" + id + "&type=" + period;
            NetworkTaskListHistory networkTaskListHistory = new NetworkTaskListHistory(url, msg);
            networkTaskListHistory.execute();
        }
    }

    private LineData generateOnlyLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        LineDataSet set = new LineDataSet(entries, pollutant);

        if(pollutant.equals("AQI")) {
            for (int i = 0; i < AQIArray.size(); i++) {
                entries.add(new Entry(i, Float.valueOf(AQIArray.get(i))));
            }
        } else if(pollutant.equals("CO")) {
            for (int i = 0; i < COArray.size(); i++) {
                entries.add(new Entry(i, Float.valueOf(COArray.get(i))));
            }
        } else if(pollutant.equals("NO2")) {
            for (int i = 0; i < NO2Array.size(); i++) {
                entries.add(new Entry(i, Float.valueOf(NO2Array.get(i))));
            }
        } else if(pollutant.equals("SO2")) {
            for (int i = 0; i < SO2Array.size(); i++) {
                entries.add(new Entry(i, Float.valueOf(SO2Array.get(i))));
            }
        } else if(pollutant.equals("O3")) {
            for (int i = 0; i < O3Array.size(); i++) {
                entries.add(new Entry(i, Float.valueOf(O3Array.get(i))));
            }
        }

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

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < AQIArray.size(); i++) {
            entries.add(new Entry(i+0.45f, Float.valueOf(AQIArray.get(i))));
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

        for (int i = 0; i < COArray.size(); i++) {
            entries.add(new BarEntry(i, Float.valueOf(COArray.get(i))));
            entries2.add(new BarEntry(i, Float.valueOf(O3Array.get(i))));
            entries3.add(new BarEntry(i, Float.valueOf(NO2Array.get(i))));
            entries4.add(new BarEntry(i, Float.valueOf(SO2Array.get(i))));
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    // to communication with Server to check ID duplication
    public class NetworkTaskListHistory extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android

        // constructor
        public NetworkTaskListHistory(String url, String values) {
            this.url = url;
            this.values = values;
        }

        // start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;       // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // get result from this "url"
            return result;
        }

        // start after done doInBackground, result will be s in this function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg;                         // msg to show to the user
            String title;                       // title of Msg
            int num = 0;
            try {
                // make JSONObject to store data from the Server
                JSONArray jsonArray = new JSONArray(s);
                JSONObject info = jsonArray.getJSONObject(0);

                AQIArray.clear();
                COArray.clear();
                NO2Array.clear();
                SO2Array.clear();
                O3Array.clear();
                xValue.clear();

                title = info.getString("status");

                //title = json_result.getString("status");                // title will be value of s's "status"
                // if user entered right email and first name
                if (title.equals("ok")) {
                    listSize = info.getInt("size");

                    if (listSize == 0) {
                        Toast.makeText(CombinedChartActivity.this, "Nothing, It has noting yet", Toast.LENGTH_SHORT).show();
                        GlobalVar.setFlag(true);
                        return;
                    } else
                        num++;

                    JSONObject jsonAir;

                    for (; num <= listSize; num++) {
                        jsonAir = jsonArray.getJSONObject(num);

                        float max;
                        xValue.add(jsonAir.getString("collected_time"));
                        COArray.add(jsonAir.getString("AQI_CO"));
                        max = Float.valueOf(COArray.get(num-1));
                        O3Array.add(jsonAir.getString("AQI_O3"));
                        if (max < Float.valueOf(O3Array.get(num-1)))
                            max = Float.valueOf(O3Array.get(num-1));
                        NO2Array.add(jsonAir.getString("AQI_NO2"));
                        if (max < Float.valueOf(NO2Array.get(num-1)))
                            max = Float.valueOf(NO2Array.get(num-1));
                        SO2Array.add(jsonAir.getString("AQI_SO2"));
                        if (max < Float.valueOf(SO2Array.get(num-1)))
                            max = Float.valueOf(SO2Array.get(num-1));
                        AQIArray.add(String.valueOf(max));
                    }
                    Toast.makeText(CombinedChartActivity.this, "all new setted", Toast.LENGTH_SHORT).show();
                } else {
                    msg = "Msg : " + info.getString("msg");
                    Toast.makeText(CombinedChartActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                Toast.makeText(CombinedChartActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            GlobalVar.setFlag(true);

            CombinedData data = new CombinedData();

            if(pollutant.equals("All")) {
                mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                });
                data.setData(generateLineData());
                data.setData(generateBarData());
                mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
            } else {
                data.setData(generateOnlyLineData());
                data.setData(generateBarData());
            }
            mChart.setData(data);
            mChart.animateY(1000);
            mChart.invalidate();

        }

    }
}