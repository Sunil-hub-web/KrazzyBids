package com.example.krazzybids.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.example.krazzybids.LoadingDialogBar;
import com.example.krazzybids.R;
import com.example.krazzybids.SessionManager;
import com.example.krazzybids.databinding.ActivityLoginPageBinding;
import com.example.krazzybids.modelclass.UserDetails;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    ActivityLoginPageBinding binding;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 40;
    SignInClient oneTapClient;
    SessionManager sessionManager;
    LoadingDialogBar loadingDialogBar;

    public static final String TAG = "LoginPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login_page);
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        oneTapClient = Identity.getSignInClient(this);

        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        loadingDialogBar = new LoadingDialogBar(this);

        binding.btnletmein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.editUserFullName.getText().toString().trim().equals("")) {

                    binding.editUserFullName.requestFocus();
                    binding.editUserFullName.setError("Enter Mobile Number");

                } else {

                    if (isValidNumber(binding.editUserFullName.getText().toString().trim())) {

                        if (binding.editUserFullName.getText().toString().trim().length() != 10) {

                            binding.editUserFullName.requestFocus();
                            binding.editUserFullName.setError("Enter 10 digit Mobile Number");

                        } else {

                            loginUserDetails(binding.editUserFullName.getText().toString().trim(),"mobile");
                        }

                    } else {

                        if (isValidEmail(binding.editUserFullName.getText().toString().trim())) {

                            loginUserDetails(binding.editUserFullName.getText().toString().trim(),"email");

                        } else {

                            binding.editUserFullName.requestFocus();
                            binding.editUserFullName.setError("Enter valide Email Id");
                        }
                    }
                }

            }
        });

        binding.cardProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, options);

//        signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();

        binding.relGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

    }
    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;

        //final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";

        pattern = Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(email);

        return matcher.matches();

        //return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    public boolean isValidNumber(final String mobile) {

        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        //pattern = Patterns.pattern;
        matcher = pattern.matcher(mobile);

        return matcher.matches();

        //return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    public void signIn() {

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

                SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = googleCredential.getGoogleIdToken();


            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }
    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    //FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if(user != null){

                            UserDetails userDetails = new UserDetails(user.getUid(),
                                    user.getDisplayName(), user.getEmail(),
                                    user.getPhotoUrl().toString());

                            sopcialMediaLogin(user.getEmail());

                        }

                      //  updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        //updateUI(null);
                    }

                   // assert user != null;

                }

            }
        });

    }
    public void loginUserDetails(String user_data, String type){

        binding.cardProgressbar.setVisibility(View.VISIBLE);
        binding.btnletmein.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                binding.cardProgressbar.setVisibility(View.GONE);
                binding.btnletmein.setVisibility(View.VISIBLE);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    String loginType = jsonObject.getString("loginType");
                    String userid = jsonObject.getString("userid");

                    if (status.equals("200")){

                        if (loginType.equals("1")){

                            String otp = jsonObject.getString("otp");

                            String struserdata = user_data+","+type;

                            sessionManager.setUSERID(userid);
                            sessionManager.setOTP(otp);
                            sessionManager.setcustdata(struserdata);

                            Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginPage.this, OTPVerifactionPage.class));

                        }else{

                            String _token = jsonObject.getString("_token");
                            sessionManager.setUSERID(userid);
                            sessionManager.setFcmToken(_token);
                            sessionManager.setLogin();
                            startActivity(new Intent(LoginPage.this, DashBoard.class));
                        }

                    }else{

                        Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                binding.cardProgressbar.setVisibility(View.GONE);
                binding.btnletmein.setVisibility(View.VISIBLE);

                Toast.makeText(LoginPage.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_data",user_data);
                params.put("type",type);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(LoginPage.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }
    public void sopcialMediaLogin(String user_data){

        loadingDialogBar.ShowDialog("User Login Wait.....");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.socialmediaSignin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialogBar.HideDialog();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    String _token = jsonObject.getString("_token");
                    String userid = jsonObject.getString("userid");

                    if (status.equals("200")){

                            sessionManager.setUSERID(userid);
                            sessionManager.setFcmToken(_token);
                            sessionManager.setLogin();

                            Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginPage.this, DashBoard.class));

                    }else{

                        Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingDialogBar.HideDialog();
                Toast.makeText(LoginPage.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_data",user_data);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(LoginPage.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sessionManager.isLogin()){

            startActivity(new Intent(LoginPage.this,DashBoard.class));
        }
    }
}