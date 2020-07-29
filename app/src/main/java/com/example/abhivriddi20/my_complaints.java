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
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.Random;

import static com.example.abhivriddi20.Auth.session;

public class my_complaints extends AppCompatActivity {

  //  String load_complaints = "http://192.168.56.1/abhivriddi2/retrive_user_complaints.php";
    String load_complaints = "https://abhivriddi.000webhostapp.com/retrive_user_complaints.php";
 //   String delete_complaints="http://192.168.56.1/abhivriddi2/delete_complaint.php";
    String delete_complaints="https://abhivriddi.000webhostapp.com/delete_complaint.php";

    List<complaint_class> productList;
    List<complaint_class> fetched_productList;
    List<complaint_class> filtered_productList;
    RecyclerView recyclerView;

    TextView search_box,list_size;

    ProductsAdapter adapter;
    AlertDialog.Builder builder;
    String post_options[]={"Delete complaint"};

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(my_complaints.this,Dashboard.class));
    }

    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);
        recyclerView = findViewById(R.id.recylcerView);

        parentLayout=findViewById(R.id.MainLayout);
        Snackbar snackbar = Snackbar
                .make(parentLayout, "Long press on complaint to delete", Snackbar.LENGTH_LONG);
        snackbar.show();

        list_size=findViewById(R.id.list_size);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        search_box=(TextView)findViewById(R.id.searchText);


        builder = new AlertDialog.Builder(this);
        builder.setTitle("");

        //initializing the productlist
        productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        loadProducts();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(my_complaints.this, "long press to delete complaint", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongItemClick(View view, final int position) {
                builder.setItems(post_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(filtered_productList!=null)
                        delete_Complaint(filtered_productList.get(position).getName());
                        else
                            Toast.makeText(my_complaints.this, "Try again", Toast.LENGTH_SHORT).show();

                        //startActivity(new Intent(my_complaints.this,my_complaints.class));
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
                if(search_box.getText().toString().equals("") || s==null) {
                    //productList = fetched_productList;
                    //filtered_productList=productList;

                   /* productList=filtered_productList;
                    adapter = new ProductsAdapter(my_complaints.this, productList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();*/
                    solved_unsolved();


                }
                else {
                    //filter(s.toString());
                    solved_unsolved();


                }

            }
        });


    }
    public void solved_unsolved()
    {
        CheckBox solved=findViewById(R.id.solved);
        CheckBox unsolved=findViewById(R.id.unsolved);
        String text=search_box.getText().toString();

        if(solved.isChecked())
        {
            if(unsolved.isChecked())
            {
                filtered_productList=fetched_productList;
                //filter(text);
                adapter = new ProductsAdapter(my_complaints.this, filtered_productList);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                list_size.setText("number of complaint fetched: "+String.valueOf(filtered_productList.size()));


            }
            else {
                prepare_filterlist(1);
               // filter(text);
            }
        }
        else {
            prepare_filterlist(0);
           // filter(text);
        }
        if(!(solved.isChecked())&& !(unsolved.isChecked())){

            filtered_productList=fetched_productList;
            adapter = new ProductsAdapter(my_complaints.this, filtered_productList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            list_size.setText("number of complaint fetched: "+String.valueOf(filtered_productList.size()));

            // filter(text);
        }
        if(text.equals(""))
        {
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }
        if(!(text.equals("")))
        {
            filter(text);
        }

    }
    public void solved_btn(View v)
    {
        solved_unsolved();
    }


    public void prepare_filterlist(int n)
    {
        List<complaint_class> temp = new ArrayList();
            for(complaint_class d: productList){
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches

                if(d.getstatus().contains("solved")&&n==1){
                    temp.add(d);
                }
                if(n==0&&!(d.getstatus().contains("solved")))
                {
                    temp.add(d);
                }
            //update recyclerview
            filtered_productList=temp;
            adapter = new ProductsAdapter(my_complaints.this, filtered_productList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            list_size.setText("number of complaint fetched: "+String.valueOf(filtered_productList.size()));

            }
    }
    void filter(String text){
        List<complaint_class> temp = new ArrayList();
        for(complaint_class d: filtered_productList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if(d.getName().contains(text)||d.getByuname().contains(text)||d.getproblem().contains(text)||d.getstatus().contains(text)||d.getregDate().contains(text)){
                    temp.add(d);
            }
        }
        //update recyclerview
        filtered_productList=temp;
        adapter = new ProductsAdapter(my_complaints.this, filtered_productList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list_size.setText("number of complaint fetched: "+String.valueOf(filtered_productList.size()));
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
                                Toast.makeText(my_complaints.this, "cid= "+ass, Toast.LENGTH_SHORT).show();
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
                                        heroObject.getString("dept"),
                                        heroObject.getString("longitude"),
                                        heroObject.getString("latitude"),
                                        address




                                ));
                              //  filtered_productList.clear();
                              //  filtered_productList=productList;
                            }
                            Toast.makeText(my_complaints.this, "cid= "+ass, Toast.LENGTH_SHORT).show();

                            //creating adapter object and setting it to recyclerview
                            fetched_productList=productList;
                            filtered_productList=productList;
                            list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));
                            adapter = new ProductsAdapter(my_complaints.this, productList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list_size.setText("something went wrong ");

                        } catch (IOException e) {
                            e.printStackTrace();
                            list_size.setText("something went wrong");

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        list_size.setText("Network problem.");

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();


                parameters.put("username",session.getusename());

                return parameters;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);


    }
    public void delete_Complaint(final String to_be_deleted_cid)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, delete_complaints,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //  progressBar.setVisibility(View.INVISIBLE);
                        //  progressBar.setVisibility(View.INVISIBLE);
                        response = response.replaceAll("\\s", "");
                        if (response.equalsIgnoreCase("success"))
                        {

                            Snackbar snackbar = Snackbar
                                    .make(parentLayout, "complaint deleted successfully", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            productList.clear();
                            fetched_productList.clear();
                            filtered_productList.clear();
                            loadProducts();
                            list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));

                            //startActivity(new Intent(my_complaints.this,my_complaints.class));

                        }
                      else
                          Toast.makeText(my_complaints.this, response, Toast.LENGTH_LONG).show();
                        list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();



                parameters.put("cid", to_be_deleted_cid);
                return parameters;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
