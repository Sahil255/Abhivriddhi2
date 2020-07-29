package com.example.abhivriddi20;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class maps_complaints extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    String load_complaints = "https://abhivriddi.000webhostapp.com/get_all_data.php";

    ProgressBar progressBar;

    View parentLayout;


    List<complaint_class> productList;
    List<complaint_class> fetched_productList;
    List<complaint_class> filtered_productList;
    RecyclerView recyclerView;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_complaints);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressBar=findViewById(R.id.progressBar);

    productList = new ArrayList<>();
/*        recyclerView = findViewById(R.id.recylcerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        parentLayout=findViewById(R.id.MainLayout);
        recyclerView.setLayoutManager(layoutManager);*/

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);

        int i;
        loadProducts();
      /* for(i=0;i<productList.size();i++) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(productList.get(i).getLat()),Double.parseDouble(productList.get(i).getLat()))).title(productList.get(i).getName())).showInfoWindow();
        }*/
    }



    URL url;
    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);

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
                                //list_size.setText("No complaints");
                                Toast.makeText(maps_complaints.this, "no complaints", Toast.LENGTH_SHORT).show();
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

                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(heroObject.getString("latitude")),Double.parseDouble(heroObject.getString("longitude")))).title(heroObject.getString("cid")).snippet("by- "+heroObject.getString("user")+"\n"+"details- "+heroObject.getString("details"))).showInfoWindow();

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(heroObject.getString("latitude")), Double.parseDouble(heroObject.getString("longitude"))), 12.0f));

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
                                        "Location"





                                ));
                            }
                            progressBar.setVisibility(View.GONE);


                            /*for(int i = 0; i< productList.size(); i++)
                            {
                                Bitmap bmImg = Ion.with(getApplicationContext())
                                        .load(productList.get(i).getImage()).asBitmap().get();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bmImg, 100, 100, false);


                               // Toast.makeText(maps_complaints.this, "cid= "+ass, Toast.LENGTH_SHORT).show();
                               // Ion.getDefault(getApplicationContext()).getCache().clear();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(productList.get(i).getLat()),Double.parseDouble(productList.get(i).getLong()))).title(productList.get(i).getName()).snippet(productList.get(i).getByuname()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))).showInfoWindow();

                            }*/

                           /* //creating adapter object and setting it to recyclerview
                            fetched_productList=productList;
                           // list_size.setText("number of complaint fetched: "+String.valueOf(productList.size()));
                            adapter = new ProductsAdapter(maps_complaints.this, productList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();*/
                        } catch (JSONException e) {
                          //  list_size.setText("No complaints");
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //list_size.setText("No complaints");
                        progressBar.setVisibility(View.GONE);

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();


                parameters.put("key","1");
                parameters.put("value","1");

                return parameters;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, "title "+marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        for(complaint_class d:productList){
            if(d.getName().equals(marker.getTitle()))
            {
                ImageView image=findViewById(R.id.image_display);


                TextView text=findViewById(R.id.details);
                text.setText("cid- "+marker.getTitle()+"\n"+marker.getSnippet());
                Bitmap bmImg = null;
                try {
                    bmImg = Ion.with(getApplicationContext())
                            .load(d.getImage()).asBitmap().get();
                    image.setImageBitmap(bmImg);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // BitmapDescriptorFactory.fromBitmap(smallMarker)

            }
        }
        progressBar.setVisibility(View.GONE);


        return false;
    }
}
