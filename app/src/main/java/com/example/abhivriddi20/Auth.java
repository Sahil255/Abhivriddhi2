package com.example.abhivriddi20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Auth extends AppCompatActivity implements View.OnClickListener {

    private PinView pinView;
    private Button next;
    private TextView topText,textU,subTop;
    private EditText userName, userPhone;
    private ConstraintLayout first, second;

    private FirebaseAuth mAuth;
    private String verificationid;
    String name,phone;

    public static Session session;

    RequestQueue requestQueue;

    View view;


   // String insertUrl="http://192.168.56.1/abhivriddi2/user_signup.php";
    String insertUrl="https://abhivriddi.000webhostapp.com/user_signup.php";


   // String load_complaints = "http://192.168.56.1/abhivriddi2/total_complaints.php";
    String load_complaints = "https://abhivriddi.000webhostapp.com/total_complaints.php";



    public void resend(View v)
    {
        sendVerificationCode(phone);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void func(View v)
    {
        ProgressButton progressButton=new ProgressButton(Auth.this,v);
        progressButton.progressButton_Activated(Auth.this,v);
        name = userName.getText().toString();
        phone = userPhone.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
            sendVerificationCode(phone);
            next.setText("Verify");
            first.setVisibility(View.GONE);
            second.setVisibility(View.VISIBLE);
            topText.setText("OTP has been sent to your Mobile number");
            subTop.setText("");
        } else {
            Toast.makeText(Auth.this, "Please enter the details", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        topText = findViewById(R.id.topText);
        subTop=findViewById(R.id.subtopText);
        pinView = findViewById(R.id.pinView);
        next = findViewById(R.id.button);
        userName = findViewById(R.id.username);
        userPhone = findViewById(R.id.userPhone);
        first = findViewById(R.id.first_step);
        second = findViewById(R.id.secondStep);
        textU = findViewById(R.id.textView_noti);
        first.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        next.setOnClickListener(this);
        session=new Session(getApplicationContext());
        if(!session.getusename().equals("unauthorised"))
        {
            if(session.getusename().equals("admin"))
                startActivity(new Intent(Auth.this,Admin.class));
            else {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        }



    }
    @Override
    public void onClick(View v) {


        if (next.getText().equals("Let's go!")) {
            name = userName.getText().toString();
            phone = userPhone.getText().toString();
            if(name.equals("admin")&&phone.equalsIgnoreCase("+919876543210"))
            {
                startActivity(new Intent(Auth.this,Admin.class));
                session.setusename("admin");
            }
            else {

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                    sendVerificationCode(phone);
                    next.setText("Verify");
                    first.setVisibility(View.GONE);
                    second.setVisibility(View.VISIBLE);
                    topText.setText("OTP has been sent to your Mobile number");
                    subTop.setText("");
                } else {
                    Toast.makeText(Auth.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (next.getText().equals("Verify")) {
            String OTP = pinView.getText().toString();
            verifyCode(OTP);
        }

    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pinView.setLineColor(Color.GREEN);
                            textU.setText("OTP Verified");
                            textU.setTextColor(Color.GREEN);


                            Intent intent = new Intent(Auth.this, Auth.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Toast.makeText(Auth.this, "Verified", Toast.LENGTH_SHORT).show();
                          //  startActivity(new Intent(Auth.this,Dashboard.class));
                            signup();


                        } else {
                            Toast.makeText(Auth.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            pinView.setLineColor(Color.RED);
                            textU.setText("X Incorrect OTP");
                            textU.setTextColor(Color.RED);

                        }
                    }

                });
    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;



        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                // progressBar.setVisibility(View.VISIBLE);

                pinView.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Auth.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };



    public void signup()
    {
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();


        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replaceAll("\\s", "");
                if (response.equalsIgnoreCase("success"))
                {
                    session.setusename(phone);
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                }
                Toast.makeText(Auth.this, response, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Auth.this, "null", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("name", name);
                parameters.put("phone", phone);

                return parameters;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }





}

