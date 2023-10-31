package com.example.krazzybids.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.krazzybids.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    LoadingDialogBar loadingDialogBar;
    SessionManager sessionManager;
    String userId, token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater,container,false);

        loadingDialogBar = new LoadingDialogBar(getActivity());
        sessionManager = new SessionManager(getContext());
        userId = sessionManager.getUSERID();
        token = sessionManager.getFcmToken();
        viewProfile(userId,token);

        binding.relEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new EditFragment();
                Bundle args = new Bundle();
                args.putString("YourKey", "SchoolUniform");
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        binding.relPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new PackageFragment();
                Bundle args = new Bundle();
                args.putString("YourKey", "SchoolUniform");
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        binding.imagEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new HomeFragment();
                Bundle args = new Bundle();
                args.putString("YourKey", "SchoolUniform");
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    public void viewProfile(String userid,String token){

        loadingDialogBar.ShowDialog("View Profile Wait.....");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.viewprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialogBar.HideDialog();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    String data = jsonObject.getString("data");
                    JSONArray jsonArray = new JSONArray(data);
                    JSONObject jsonObject_data = jsonArray.getJSONObject(0);

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    if (status.equals("200")){

                        String id = jsonObject_data.getString("id");
                        String name = jsonObject_data.getString("name");
                        String email = jsonObject_data.getString("email");
                        String mobile_number = jsonObject_data.getString("mobile_number");
                        String image = jsonObject_data.getString("image");

                        binding.userName.setText(name);
                        binding.userEmailID.setText(email);

                        if (image.equals("null")){}else{
                            String url = "https://www.polosoftech.com/krazybids/public/user_image/"+image;
                            Picasso.with(getActivity()).load(url).into(binding.profileImage);
                        }


                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingDialogBar.HideDialog();
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("userid",userid);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }
}
