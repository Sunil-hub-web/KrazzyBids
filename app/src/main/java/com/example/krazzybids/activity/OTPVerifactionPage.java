package com.example.krazzybids.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.krazzybids.ApiList;
import com.example.krazzybids.SessionManager;
import com.example.krazzybids.databinding.ActivityOtpverifactionPageBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPVerifactionPage extends AppCompatActivity {

    ActivityOtpverifactionPageBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_otpverifaction_page);

        binding = ActivityOtpverifactionPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(OTPVerifactionPage.this);

        binding.btnOtpVerifay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.otpView.getOTP().equals("")){

                    Toast.makeText(OTPVerifactionPage.this, "Enter Your Otp", Toast.LENGTH_SHORT).show();

                } else if (binding.otpView.getOTP().length() != 6) {

                    Toast.makeText(OTPVerifactionPage.this, "Enter Your 6 digit Otp", Toast.LENGTH_SHORT).show();

                }else{

                    String userdata = sessionManager.getcustdata();
                    String userid = sessionManager.getUSERID();
                    String str_otp = sessionManager.getOTP();
                    // split string from space
                    String[] result = userdata.split(",");

                    OTPVerifaction(userid,str_otp,result[1],result[0]);



                }

            }
        });

//        binding.cardProgressbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                binding.cardProgressbar.setVisibility(View.GONE);
//                binding.btnOtpVerifay.setVisibility(View.VISIBLE);
//            }
//        });

        timer();

    }

    public void timer(){

        //Initialize time duration
        long duration = TimeUnit.MINUTES.toMillis(1);
        //Initialize countdown timer

        new CountDownTimer(duration, 5) {
            @Override
            public void onTick(long millisUntilFinished) {

                //When tick
                //Convert millisecond to minute and second

                String sDuration = String.format(Locale.ENGLISH,"%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                binding.textTimer.setText(sDuration);

                binding.textTimer.setVisibility(View.VISIBLE);
                binding.resendtext.setVisibility(View.VISIBLE);
                binding.resendOTP.setVisibility(View.GONE);

            }

            @Override
            public void onFinish() {

                binding.textTimer.setVisibility(View.GONE);
                binding.resendtext.setVisibility(View.GONE);
                binding.resendOTP.setVisibility(View.VISIBLE);

            }
        }.start();
    }

    public void OTPVerifaction(String userid,String otp,String type, String user_data){

        binding.cardProgressbar.setVisibility(View.VISIBLE);
        binding.btnOtpVerifay.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.otpverifay, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                binding.cardProgressbar.setVisibility(View.GONE);
                binding.btnOtpVerifay.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    String _token = jsonObject.getString("_token");
                    String userid = jsonObject.getString("userid");

                    if (status.equals("200")){

                        sessionManager.setFcmToken(_token);
                        sessionManager.setUSERID(userid);
                        sessionManager.setLogin();

                        Toast.makeText(OTPVerifactionPage.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OTPVerifactionPage.this,DashBoard.class));

                    }else{

                        Toast.makeText(OTPVerifactionPage.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                binding.cardProgressbar.setVisibility(View.GONE);
                binding.btnOtpVerifay.setVisibility(View.VISIBLE);

                Toast.makeText(OTPVerifactionPage.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("userid",userid);
                params.put("otp",otp);
                params.put("type",type);
                params.put("user_data",user_data);

                Log.d("userDataList",params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPVerifactionPage.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }
}