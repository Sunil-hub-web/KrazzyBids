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
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.krazzybids.ApiList;
import com.example.krazzybids.LoadingDialogBar;
import com.example.krazzybids.R;
import com.example.krazzybids.activity.LoginPage;
import com.example.krazzybids.adapter.PackageAdapter;
import com.example.krazzybids.databinding.FragmentPackagesBinding;
import com.example.krazzybids.modelclass.Package_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class PackageFragment extends Fragment {

    FragmentPackagesBinding binding;
    LoadingDialogBar loadingDialogBar;
    ArrayList<Package_Model> packageModels = new ArrayList<>();
    PackageAdapter packageAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPackagesBinding.inflate(inflater,container,false);

        loadingDialogBar = new LoadingDialogBar(getActivity());

        binding.imagePackageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new ProfileFragment();
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

        pacckageDetails();

        return binding.getRoot();
    }

    public void pacckageDetails(){

        loadingDialogBar.ShowDialog("View Package Wait.....");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiList.packages, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialogBar.HideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    String data = jsonObject.getString("data");

                    JSONArray jsonArray_data = new JSONArray(data);
                    for (int i=0;i<jsonArray_data.length();i++){

                        JSONObject jsonObject1_data = jsonArray_data.getJSONObject(i);
                        String id = jsonObject1_data.getString("id");
                        String name = jsonObject1_data.getString("name");
                        String total_price = jsonObject1_data.getString("total_price");
                        String total_bids = jsonObject1_data.getString("total_bids");
                        String discount_price = jsonObject1_data.getString("discount_price");
                        String status_data = jsonObject1_data.getString("status");
                        String create_at = jsonObject1_data.getString("create_at");

                        Package_Model package_model = new Package_Model(
                                id, name, total_price, total_bids, discount_price, status_data, create_at
                        );

                        packageModels.add(package_model);
                    }

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
                    packageAdapter = new PackageAdapter(packageModels,getActivity());
                    binding.packagesRecycler.setLayoutManager(gridLayoutManager);
                    binding.packagesRecycler.setHasFixedSize(true);
                    binding.packagesRecycler.setAdapter(packageAdapter);

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
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
}
