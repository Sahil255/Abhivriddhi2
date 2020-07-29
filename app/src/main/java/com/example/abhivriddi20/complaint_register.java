package com.example.abhivriddi20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.example.abhivriddi20.Auth.session;


public class complaint_register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    LinearLayout image_layout, problem_layout;
    private Spinner selectionSpinner;


    public static LocationManager locationManager;
    public static LocationListener locationListener;

    private int GPS_btn = 1;
    public static int set_loc = 0;
    public static LatLng locate;
    private GoogleMap googleMap;
    private MapView mapView;


    //private Button btn;
    private static final int PICK_IMAGE_ID = 234;
    private String dept = "Electric";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int courselocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readmsgPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int recievemsgPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (courselocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (readmsgPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (recievemsgPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }


    public void uploadImage(View v) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            locate = new LatLng(location.getLatitude(), location.getLongitude());
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            String address = "";
                            try {
                                List<Address> addressList = geocoder.getFromLocation(locate.latitude, locate.longitude, 1);

                                Log.i("place---", String.valueOf(addressList));
                                message_box.setText("current location :  " + addressList.get(0).getAddressLine(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                                message_box.setText(e.getMessage());
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    };
                    SingleShotLocationProvider.requestSingleUpdate(this,
                            new SingleShotLocationProvider.LocationCallback() {
                                @Override
                                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                    //  Toast.makeText(HomeActivity.this, String.valueOf(location.latitude) ,Toast.LENGTH_SHORT).show();
                                    locate = new LatLng(location.latitude, location.longitude);

                                    Log.i("Location", "my location is " + String.valueOf(location.latitude));

                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    String address = "";
                                    try {
                                        List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);

                                        Log.i("place---", String.valueOf(addressList));
                                        message_box.setText("current location :  " + addressList.get(0).getAddressLine(0));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        message_box.setText(e.getMessage());
                                    }

                                }
                            });
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);


                    Log.i("Location", "granted");
                }
            }
        }
    }


    public static TextView message_box;
    public String username;
    EditText prblm;
    ImageView imageView, small_imageview;
    Bitmap bitmap;
    //String urlUpload = "http://192.168.56.1/abhivriddi2/register_complaint.php";
    String urlUpload = "https://abhivriddi.000webhostapp.com/register_complaint.php";

    public void recenter_loc(View v)
    {
        set_loc=0;
        locationManager=(LocationManager)this.getSystemService(LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = "";
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    Log.i("place---", String.valueOf(addressList));
                    message_box.setText("current location :  " + addressList.get(0).getAddressLine(0));
                }catch (Exception e)
                {
                    e.printStackTrace();
                    message_box.setText(e.getMessage());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(complaint_register.this, "GPS enabled", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onProviderDisabled(String provider) {

                Toast.makeText(complaint_register.this, "GPS Disabled", Toast.LENGTH_SHORT).show();


                GPS_btn=0;

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastloc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(locationNet!=null)
            {
                // Toast.makeText(this, String.valueOf(locationNet.getLatitude()), Toast.LENGTH_SHORT).show();


            }
            else
            {
                Toast.makeText(this, "Unknown Location", Toast.LENGTH_SHORT).show();
            }
            if(lastloc!=null)
            {
                //Toast.makeText(HomeActivity.this, String.valueOf(lastloc.getLatitude()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Unknown", Toast.LENGTH_SHORT).show();
            }

        }

        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        //  Toast.makeText(HomeActivity.this, String.valueOf(location.latitude) ,Toast.LENGTH_SHORT).show();
                        Log.i("Location", "my location is " + String.valueOf(location.latitude));


                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        String address = "";
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);

                            Log.i("place---", String.valueOf(addressList));
                            message_box.setText("got location :  " + addressList.get(0).getAddressLine(0));

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            message_box.setText(e.getMessage());
                        }

                    }
                });
    }

    public void register() {

        //uploadBitmap(bitmap);

        //message_box.setText(session.getusename() + " Please wait...");


        username = session.getusename();
        //  Toast.makeText(this, "" + username, Toast.LENGTH_SHORT).show();
        if (session.getusename().equals("unauthorised")) {
            Intent intent = new Intent(getApplicationContext(), Auth.class);
            startActivity(intent);
        }
        if (locate != null) {
            if (bitmap != null) {
                if (!prblm.getText().toString().equals("")) {
                    Random rand = new Random();
                    final String gen_cid=String.valueOf(rand.nextInt(1000000000));
                    //  progressBar.setVisibility(View.VISIBLE);
                    // btn.setVisibility(View.INVISIBLE);
                    final String tags = prblm.getText().toString().trim();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            response = response.replaceAll("\\s", "");
                            if (response.equalsIgnoreCase("success")) {
                                //in oncreate  email has been sent to registered mail id"

                                sendSms(gen_cid);
                                Toast.makeText(complaint_register.this, response + "We have sent a message to registered mobile number", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                                // progressBar.setVisibility(View.INVISIBLE);
                                //  btn.setVisibility(View.VISIBLE);
                                // login_btn.setVisibility(View.VISIBLE);
                                progressButton.progressVerify(complaint_register.this, view);
                            } else {
                                Toast.makeText(complaint_register.this, "" + response, Toast.LENGTH_SHORT).show();
                                //progressBar.setVisibility(View.INVISIBLE);
                                //  btn.setVisibility(View.VISIBLE);
                                // login_btn.setVisibility(View.VISIBLE);
                                progressButton.progressButton_deactivate(complaint_register.this, view);
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(complaint_register.this, error.toString(), Toast.LENGTH_SHORT).show();
                            progressButton.progressButton_deactivate(complaint_register.this, view);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();

                            String imageData = imageToString(bitmap);
//                Toast.makeText(Register_complaint.this, imageData, Toast.LENGTH_SHORT).show();
                            // Log.i("img data",imageData);


                            parameters.put("image", imageData);
                            parameters.put("problem", tags);
                            parameters.put("byname", session.getusename());
                            parameters.put("dept", dept);
                            parameters.put("cid", gen_cid);
                            // parameters.put("cid", "4886");
                            parameters.put("latitude", String.valueOf(locate.latitude));
                            parameters.put("longitude", String.valueOf(locate.longitude));

                            // parameters.put("password", password.getText().toString());

                            return parameters;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            120000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(complaint_register.this);
                    requestQueue.add(stringRequest);
                } else {
                    Toast.makeText(this, "Enter Problem Description", Toast.LENGTH_SHORT).show();
                    progressButton.progressButton_deactivate(complaint_register.this, view);
                }
            } else {
               // Toast.makeText(this, "Select image ", Toast.LENGTH_SHORT).show();
                Toast.makeText(complaint_register.this, ""+String.valueOf(locate.latitude)+" , "+String.valueOf(locate.longitude), Toast.LENGTH_SHORT).show();

                progressButton.progressButton_deactivate(complaint_register.this, view);
            }
        } else {
            Toast.makeText(this, "Unable to detect your locaiton,Select location manually", Toast.LENGTH_LONG).show();
            progressButton.progressButton_deactivate(complaint_register.this, view);
        }
        // progressBar.setVisibility(View.INVISIBLE);
        //btn.setVisibility(View.VISIBLE);


    }

    ///////////////////////////////////////////////////////////////////////////////////


    public void sendSms(String cid) {
        /*HttpResponse response = Unirest.post("https://www.fast2sms.com/dev/bulk")
                                        .header("authorization", "YOUR_API_KEY")
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .body("sender_id=FSTSMS&message=This%20is%20a%20test%20message&language=english&route=p&numbers=9999999999,8888888888,7777777777")
                         MediaType JSON = MediaType.parse("application/json; charset=utf-8");*/
        //String url="https://www.fast2sms.com/dev/bulk";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sender_id", "FSTSMS")
                .addFormDataPart("message", "\nDear user,\nyou have filed a complaint in Abhivriddi\n Your (complaint id) cid is: "+cid)
                .addFormDataPart("language", "english")
                .addFormDataPart("route", "p")
                .addFormDataPart("numbers", session.getusename().substring(3))
                .build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://www.fast2sms.com/dev/bulk")
                .post(requestBody)
                .addHeader("authorization", "oA05HTUezVyvkrJ36qcOxWlPpfL41umg2CNYw8DFZIGaXb7Qn95umRoPMTYIcBtdGgyXxe4kO3isrLhZ")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                // .addHeader("Postman-Token", "7e231ef9-5236-40d1-a28f-e5986f936877")
                .build();

      client.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              e.printStackTrace();
          }

          @Override
          public void onResponse(Call call, final okhttp3.Response response) throws IOException {
              if (response.isSuccessful()) {
                  final String myResponse = response.body().string();
                  //Toast.makeText(complaint_register.this, "" + myResponse, Toast.LENGTH_SHORT).show();
                  complaint_register.this.runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Log.i("responsess", myResponse);

                          //progress.hide();
                      }
                  });
              }
          }
      });
  }






//////////////////////////////////////////////////////////////////////////////////
    public void locations_fun(View v) {
        startActivity(new Intent(complaint_register.this,MapsActivity.class));
    }

    public void back(View view) {
        switch_fragments(1);
    }

    public void next(View v) {
        switch_fragments(0);
    }

    View view;
    ProgressButton progressButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(complaint_register.this,Dashboard.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_register);
        image_layout = findViewById(R.id.image_layout);
        problem_layout = findViewById(R.id.problem);
        switch_fragments(1);
        initSelectionSpinner();

        view =findViewById(R.id.myProgressButton);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressButton=new ProgressButton(complaint_register.this,view);
                progressButton.progressButton_Activated(complaint_register.this,view);
                              register();


            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);
        prblm = (EditText) findViewById(R.id.prblm);
        small_imageview = (ImageView) findViewById(R.id.small_image);


        set_loc = 0;
        message_box = (TextView) findViewById(R.id.message);

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                // Toast.makeText(Register_complaint.this, "GPS enabled", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onProviderDisabled(String provider) {

                // Toast.makeText(Register_complaint.this, "GPS Disabled", Toast.LENGTH_SHORT).show();


                GPS_btn = 0;

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastloc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationNet != null) {
                // Toast.makeText(this, String.valueOf(locationNet.getLatitude()), Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "Unknown Location", Toast.LENGTH_SHORT).show();
            }
            if (lastloc != null) {
                //Toast.makeText(HomeActivity.this, String.valueOf(lastloc.getLatitude()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown", Toast.LENGTH_SHORT).show();
            }

        }

        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        //  Toast.makeText(HomeActivity.this, String.valueOf(location.latitude) ,Toast.LENGTH_SHORT).show();
                        locate = new LatLng(location.latitude, location.longitude);

                        Log.i("Location", "my location is " + String.valueOf(location.latitude));

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        String address = "";
                        try {

                            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);

                            Log.i("place---", String.valueOf(addressList));
                            message_box.setText("current location :  " + addressList.get(0).getAddressLine(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                            message_box.setText(e.getMessage());
                        }

                    }
                });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }
    }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE_ID) {
                if (data.getExtras() == null) {
                    bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    imageView.setImageBitmap(bitmap);
                    small_imageview.setImageBitmap(bitmap);
                } else {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    small_imageview.setImageBitmap(bitmap);
                }
            }
        }
    }






        private String imageToString (Bitmap bitmap)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }

    public void switch_fragments(int n)
    {
        if(n==1)
        {
            image_layout.setVisibility(View.VISIBLE);
            problem_layout.setVisibility(View.GONE);
        }
        else
        {
            image_layout.setVisibility(View.GONE);
            problem_layout.setVisibility(View.VISIBLE);
        }
    }
    private void initSelectionSpinner () {
        selectionSpinner = findViewById(R.id.selection_spinner);


        selectionSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }
    public void onItemSelected (AdapterView < ? > parent, View view,int pos, long id){
        dept=String.valueOf(selectionSpinner.getSelectedItem());
        Toast.makeText(this, selectionSpinner.getSelectedItem() + " selected", Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected (AdapterView < ? > parent){
    }
}
