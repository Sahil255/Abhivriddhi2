package com.example.abhivriddi20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.abhivriddi20.Auth.session;

public class complaints_page extends AppCompatActivity {


    //  String load_complaints = "http://192.168.56.1/abhivriddi2/retrive_user_complaints.php";
    String load_complaints = "https://abhivriddi.000webhostapp.com/retrive_complaints.php";
    //   String delete_complaints="http://192.168.56.1/abhivriddi2/delete_complaint.php";
    String update_complaints="https://abhivriddi.000webhostapp.com/update_complaint.php";

    View parentLayout;

    List<complaint_class> productList;
    List<complaint_class> fetched_productList;
    List<complaint_class> filtered_productList;
    RecyclerView recyclerView;

    TextView search_box,list_size;

    ProductsAdapter adapter;
    String status_passed;
    AlertDialog.Builder builder;
    LinearLayout linearLayout;
    CheckBox checkBox,checkBox1,checkBox0;
    EditText add_remark;

    String selected_status;

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(complaints_page.this,Admin.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_page);
        status_passed=getIntent().getStringExtra("status_pass");
        Toast.makeText(this, ""+status_passed, Toast.LENGTH_SHORT).show();
        list_size=findViewById(R.id.list_size);


        recyclerView = findViewById(R.id.recylcerView);

        parentLayout=findViewById(R.id.MainLayout);
        Snackbar snackbar = Snackbar
                .make(parentLayout, "Long press on complaint to Update", Snackbar.LENGTH_LONG);
        snackbar.show();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        search_box=(TextView)findViewById(R.id.searchText);


        add_remark=new EditText(this);
        add_remark.setHint("ADd remark (optional)");
        checkBox0 = new CheckBox(this);
        checkBox0.setText("registered");
        checkBox = new CheckBox(this);
        checkBox.setText("under process");
        checkBox1= new CheckBox(this);
        checkBox1.setText("solved");




        builder = new AlertDialog.Builder(this);


        //initializing the productlist
        productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        loadProducts();


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(complaints_page.this, "long press to Update status of complaint", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongItemClick(View view, final int position) {
               /* builder.setItems(post_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     //   delete_Complaint(productList.get(position).getName());

                        //startActivity(new Intent(my_complaints.this,my_complaints.class));
                    }
                });
                builder.show();*/


                linearLayout = new LinearLayout(getApplicationContext());
                linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

                linearLayout.setOrientation(Integer.parseInt("1"));
                if(checkBox.getParent() != null) {
                    ((ViewGroup)checkBox.getParent()).removeView(checkBox); // <- fix
                    checkBox.setChecked(false);
                }
                if(checkBox0.getParent() != null) {
                    ((ViewGroup)checkBox0.getParent()).removeView(checkBox0); // <- fix
                    checkBox0.setChecked(false);
                }
                if(checkBox1.getParent() != null) {
                    ((ViewGroup)checkBox1.getParent()).removeView(checkBox1); // <- fix
                    checkBox1.setChecked(false);
                }
                if(add_remark.getParent() != null) {
                    ((ViewGroup)add_remark.getParent()).removeView(add_remark); // <- fix
                    add_remark.setText("");
                    add_remark.setHint("Add remark (optional)");
                }
                //layout.addView(tv);
                //layout.addView(tv);
                linearLayout.addView(checkBox0);
                linearLayout.addView(checkBox);
                linearLayout.addView(checkBox1);

                linearLayout.addView(add_remark);
                //checkBox.setGravity(Gravity.CENTER);
                linearLayout.setPadding(50,10,10,10);
                if(productList.get(position).getstatus().equals("registered")) {
                    checkBox0.setChecked(true);
                    checkBox0.setClickable(false);

                }
                if(productList.get(position).getstatus().equals("process")) {
                    checkBox0.setChecked(true);
                    checkBox0.setClickable(false);
                    checkBox.setChecked(true);
                    checkBox.setClickable(false);
                }
                if(productList.get(position).getstatus().equals("solved")) {
                    checkBox0.setChecked(true);
                    checkBox0.setClickable(false);
                    checkBox.setChecked(true);
                    checkBox.setClickable(false);
                    checkBox1.setChecked(true);
                    checkBox1.setClickable(false);
                    add_remark.setEnabled(false);
                }





                linearLayout.setGravity(Gravity.CENTER);

                builder.setView(linearLayout);
                builder.setTitle("select progress of complaint for "+productList.get(position).getName());
              //  builder.setMessage("select progress of complaint for "+productList.get(position).getName());
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something
                        if(checkBox0.isChecked())
                        {
                            selected_status="registered";
                        }
                        if(checkBox.isChecked())
                        {
                            selected_status ="process";
                        }

                        if(checkBox1.isChecked())
                        {
                            selected_status="solved";
                        }
                        String remark_to_pass;
                        if(add_remark.getText().toString().equals(""))
                            remark_to_pass="No remark";
                        else
                            remark_to_pass=add_remark.getText().toString();
                        update(productList.get(position).getName(),selected_status,remark_to_pass);
                        Toast.makeText(complaints_page.this, "msg "+add_remark.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        }));


        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
                //solved_unsolved();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input

                //you can use runnable postDelayed like 500 ms to delay search text
                if((search_box.getText().toString().equals("") || s==null)&&fetched_productList.size()!=0) {
                    productList = fetched_productList;
                    //filtered_productList=productList;

                    //productList=filtered_productList;
                    adapter = new ProductsAdapter(complaints_page.this, productList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));

                }
                else {
                    try {
                        filter(s.toString());
                    }catch (Exception e) {
                        list_size.setText(e.getMessage());
                    }

                }

            }
        });

    }
    void filter(String text){
        List<complaint_class> temp = new ArrayList();
        for(complaint_class d:productList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if(d.getName().contains(text)||d.getByuname().contains(text)||d.getproblem().contains(text)||d.getstatus().contains(text)||d.getregDate().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        productList=temp;
        adapter = new ProductsAdapter(complaints_page.this, productList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));
        //productList = temp;
        //adapter.notifyDataSetChanged();
        //ProductsAdapter.updateList(temp);
    }

    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, load_complaints,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //  progressBar.setVisibility(View.INVISIBLE);
                        //  progressBar.setVisibility(View.INVISIBLE);


                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray heroArray = obj.getJSONArray("stuff");
                            if(heroArray.length()==0)
                                list_size.setText("No complaints");
                            String ass="";
                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject heroObject = heroArray.getJSONObject(i);

                                // data+=heroObject.getString("cid");
                                //data+=heroObject.getString("byuname");
                                //creating a hero object and giving them the values from json object
                                // Hero hero = new Hero(heroObject.getString("cid"), heroObject.getString("byuname"));
                                //Toast.makeText(Admin.this, heroObject.getString("byuname"), Toast.LENGTH_SHORT).show();
                                ass+=heroObject.getString("cid");
                                Toast.makeText(complaints_page.this, "cid= "+ass, Toast.LENGTH_SHORT).show();
                                String address="";
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addressList = geocoder.getFromLocation(Double.valueOf(heroObject.getString("latitude")), Double.valueOf(heroObject.getString("longitude")), 1);

                                Log.i("place", String.valueOf(addressList));
                                if (addressList != null && addressList.size() > 0) {
                                    if(addressList.get(0).getFeatureName()!=null)
                                        address+=addressList.get(0).getFeatureName()+", ";

                                    if (addressList.get(0).getSubAdminArea() != null ) {
                                        address += addressList.get(0).getSubAdminArea();
                                    }
                                    else
                                    {
                                        if(addressList.get(0).getAdminArea()!=null)
                                            address+=addressList.get(0).getAdminArea();
                                    }
                                }

                                //adding the product to product list
                                productList.add(new complaint_class(
                                        heroObject.getString("cid"),
                                        "https://abhivriddi.000webhostapp.com/"+heroObject.getString("image"),
                                        // "http://192.168.56.1/abhivriddi2/"+heroObject.getString("image"),
                                        heroObject.getString("user"),
                                        heroObject.getString("details"),
                                        heroObject.getString("status"),
                                        heroObject.getString("regDate"),
                                        heroObject.getString("updatedDate"),
                                        heroObject.getString("remarks"),
                                        heroObject.getString("department"),
                                        heroObject.getString("longitude"),
                                        heroObject.getString("latitude"),
                                        address





                                ));
                            }
                            Toast.makeText(complaints_page.this, "cid= "+ass, Toast.LENGTH_SHORT).show();

                            //creating adapter object and setting it to recyclerview
                            fetched_productList=productList;
                            list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));
                            adapter = new ProductsAdapter(complaints_page.this, productList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            list_size.setText("Network issue ,restart the app");

                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        list_size.setText("Something went wrong");
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();


                parameters.put("key","status");
                parameters.put("value",status_passed);

                return parameters;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);


    }


    public void update(final String cid_sent,final String status_sent, final String remarks_sent)
    {

        StringRequest request = new StringRequest(Request.Method.POST, update_complaints, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replaceAll("\\s", "");
                if (response.equalsIgnoreCase("success"))
                {
                    //session.setusename(phone);
                    Intent intent=new Intent(complaints_page.this,complaints_page.class);
                    intent.putExtra("status_pass",status_passed);
                    startActivity(intent);
                }
                Toast.makeText(complaints_page.this, response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAGing", "Failed with error msg:\t" + error.getMessage());
                Log.i("TAGing", "Error StackTrace: \t" + error.getStackTrace());

                // edited here
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e("TAGing", new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (error.getMessage() == null){
                    //signup();
                    Toast.makeText(complaints_page.this, "null", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("cid",cid_sent);
                parameters.put("status",status_sent);
                parameters.put("remarks", remarks_sent);

                return parameters;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(this).add(request);

    }
}
