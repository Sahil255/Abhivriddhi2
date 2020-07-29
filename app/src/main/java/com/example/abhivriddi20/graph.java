package com.example.abhivriddi20;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class graph extends AppCompatActivity {

    TextView total, solved;
    RequestQueue requestQueue;
    //String load_complaints = "http://192.168.56.1/abhivriddi2/total_complaints.php";
    String load_complaints = "http://abhivriddi.000webhostapp.com/dept_wise.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        load_total_complaints_table();
    }

    public void load_total_complaints_table() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, load_complaints, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);


                    JSONArray heroArray = obj.getJSONArray("stuff");

                    Toast.makeText(graph.this, "fffff", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < heroArray.length(); i++) {

                        JSONObject heroObject = heroArray.getJSONObject(i);





                        BarChart barChart = (BarChart) findViewById(R.id.barchart);
                        ArrayList<BarEntry> entries1 = new ArrayList<>();
                        entries1.add(new BarEntry(1,Float.valueOf(heroObject.getString("solved"))));
                        //entries1.add(new BarEntry(1,2));
                       // entries1.add(new BarEntry(2,4));

                        ArrayList<BarEntry> entries2 = new ArrayList<>();
                       //  entries2.add(new BarEntry(1,2));
                        entries2.add(new BarEntry(1,Float.valueOf(heroObject.getString("total"))));



                        BarDataSet bardataset1 = new BarDataSet(entries1, "complaint solved");
                        bardataset1.setColor(Color.GREEN);
                        BarDataSet bardataset2 = new BarDataSet(entries2, "Complaints reigseterd");
                        bardataset2.setColor(Color.RED);




                        String[] label=new String[]{"Complaints "};

                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
                        xAxis.setCenterAxisLabels(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1);
                        xAxis.setGranularityEnabled(true);
                        BarData data = new BarData(bardataset1,bardataset2);
                        barChart.setData(data); // set the data and list of labels into chart
                        //barChart.setDescription("Set Bar Chart Description Here");  // set the description
                        //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                        barChart.animateY(5000);
                        barChart.setDragEnabled(true);
                        barChart.setVisibleXRangeMaximum(3);

                        float barSpace=0.1f;
                        float groupSpace=0.5f;
                        data.setBarWidth(0.15f);
                        barChart.getXAxis().setAxisMinimum(0);
                        barChart.groupBars(0,groupSpace,barSpace);
                        barChart.invalidate();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(graph.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(graph.this, "Failed" + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("infooo", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                return parameters;
            }
        };
        requestQueue.add(request);
    }
}