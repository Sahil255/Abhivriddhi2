package com.example.abhivriddi20;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.abhivriddi20.Auth.session;
import static com.example.abhivriddi20.complaint_register.locate;
import static com.example.abhivriddi20.complaint_register.message_box;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent=getIntent();


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng sydney;
        if((locate!=null)) {
            sydney = new LatLng(locate.latitude, locate.longitude);
        }
        else
        {
            sydney = new LatLng(-34, 121);
        }
        mMap.addMarker(new MarkerOptions().position(sydney).title("location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = "";
                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    locate=latLng;

                    Log.i("place", String.valueOf(latLng));
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
                        //  Toast.makeText(MapsActivity.this, address, LENGTH_SHORT).show();
                        complaint_register.set_loc=1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(MapsActivity.this, address, LENGTH_SHORT).show();
                //if (address == "") {
                message_box.setText(address);

                address+=" \t ";

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date;
                String add=address;
                date=sdf.format(new Date());

                // }

                mMap.clear();
                mMap.addMarker(new MarkerOptions().title(add).position(latLng).snippet(date)).showInfoWindow();
                address +="( "+ sdf.format(new Date())+" )";
                //   MainActivity.locations.add(latLng);
                //  MainActivity.arrayList.add(address);

                //   MainActivity.adapter.notifyDataSetChanged();

                Toast.makeText(MapsActivity.this, "Location saved successfully as\n "+address+" "+locate.latitude+" , "+locate.longitude, LENGTH_SHORT).show();

            }
        });

        /*SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        //  Toast.makeText(HomeActivity.this, String.valueOf(location.latitude) ,Toast.LENGTH_SHORT).show();
                        locate=new LatLng(location.latitude,location.longitude);

                        mMap.addMarker(new MarkerOptions().title("here!").position(locate)).showInfoWindow();
                        Log.i("Location", "my location is " + String.valueOf(location.latitude));

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        String address = "";
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);

                            Log.i("place---", String.valueOf(addressList));
                            message_box.setText("current location :  " + addressList.get(0).getAddressLine(0));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            message_box.setText(e.getMessage());
                        }

                    }
                });*/

    }
}
