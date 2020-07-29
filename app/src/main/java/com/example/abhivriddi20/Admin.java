package com.example.abhivriddi20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.abhivriddi20.Auth.session;

public class Admin extends AppCompatActivity {


    public void toMaps_func(View v)
    {
        startActivity(new Intent(Admin.this,maps_complaints.class));
    }
    public void not_yet_processed_func(View v)
    {
        Intent intent = new Intent(Admin.this, complaints_page.class);
        intent.putExtra("status_pass","registered");
        startActivity(intent);
    }
    public void under_process_func(View v)
    {
        Intent intent = new Intent(Admin.this, complaints_page.class);
        intent.putExtra("status_pass","process");
        startActivity(intent);
    }
    public void solved_func(View v)
    {
        Intent intent = new Intent(Admin.this, complaints_page.class);
        intent.putExtra("status_pass","solved");
        startActivity(intent);
    }

SwipeRefreshLayout swipeRefresh;
    RequestQueue requestQueue;
    Spinner dynamicListSpinner;
    // String load_complaints = "http://192.168.56.1/abhivriddi2/total_complaints.php";
    String load_complaints = "https://abhivriddi.000webhostapp.com/dept_wise.php";

    ArrayList<BarEntry> entries1;
    ArrayList<BarEntry> entries2;
    ArrayList<BarEntry> entries3;
    ArrayList<String> labels_list;
    String[] label;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent intent;
        entries1 = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();
        labels_list = new ArrayList<>();
        label=new String[]{};

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                entries1 = new ArrayList<>();
                entries2 = new ArrayList<>();
                entries3 = new ArrayList<>();
                labels_list = new ArrayList<>();
                label=new String[]{};
                load_total_complaints_table1();
            }
        });
        load_total_complaints_table1();
        initDynamicListSpinner();
        dynamicListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Admin.this, "item: "+dynamicListSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                if(dynamicListSpinner.getSelectedItem().toString().equals("Logout"))
                {
                    session.logout();
                    startActivity(new Intent(Admin.this,Auth.class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initDynamicListSpinner() {
        dynamicListSpinner = findViewById(R.id.custom_list_spinner);


        // Custom choices
        List<CharSequence> choices = new ArrayList<>();
        choices.add(session.getusename());
        choices.add("Logout");


        // Create an ArrayAdapter with custom choices
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choices);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to th spinner
        dynamicListSpinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //  Toast.makeText(this,  parent.getSelectedItem() + " selected", Toast.LENGTH_SHORT).show();


    }

    public void onNothingSelected(AdapterView<?> parent) {
    }




    public void load_total_complaints_table1() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, load_complaints, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);


                    JSONArray heroArray = obj.getJSONArray("stuff1");

                    // Toast.makeText(Dashboard.this, "Loading graph", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < heroArray.length(); i++) {

                        JSONObject heroObject = heroArray.getJSONObject(i);




                        //total.setText(heroObject.getString("total"));
                        //solved.setText(heroObject.getString("solved"));




                        entries1.add(new BarEntry(i+1,Float.valueOf(heroObject.getString("solvedC"))));
                        //entries1.add(new BarEntry(1,2));
                        // entries1.add(new BarEntry(2,4));


                        //  entries2.add(new BarEntry(1,2));
                        entries2.add(new BarEntry(i+1,Float.valueOf(heroObject.getString("registeredC"))));
                        entries3.add(new BarEntry(i+1,Float.valueOf(heroObject.getString("processC"))));
                        labels_list.add(heroObject.getString("department"));

                    }
                    BarChart barChart = (BarChart) findViewById(R.id.barchart);

                    BarDataSet bardataset1 = new BarDataSet(entries1, "complaint solved");
                    bardataset1.setColor(Color.GREEN);
                    BarDataSet bardataset2 = new BarDataSet(entries2, "Complaints reigseterd");
                    bardataset2.setColor(Color.RED);
                    BarDataSet bardataset3 = new BarDataSet(entries3, "under process");
                    bardataset3.setColor(Color.YELLOW);






                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels_list));
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1);
                    xAxis.setDrawGridLines(false);
                    xAxis.setGranularityEnabled(true);
                    BarData data = new BarData(bardataset2,bardataset3,bardataset1);
                    barChart.setData(data); // set the data and list of labels into chart
                    //barChart.setDescription("Set Bar Chart Description Here");  // set the description
                    //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    barChart.animateY(5000);
                    barChart.setDragEnabled(true);
                    barChart.setVisibleXRangeMaximum(3);

                    float barSpace=0.01f;
                    float groupSpace=0.5f;
                    data.setBarWidth(0.15f);
                    barChart.getXAxis().setAxisMinimum(0);
                    barChart.groupBars(0,groupSpace,barSpace);
                    barChart.invalidate();
                    swipeRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Admin.this, "Failed" + error.getMessage(), Toast.LENGTH_LONG).show();
                //Log.i("infooo", error.getMessage());
                swipeRefresh.setRefreshing(false);
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
