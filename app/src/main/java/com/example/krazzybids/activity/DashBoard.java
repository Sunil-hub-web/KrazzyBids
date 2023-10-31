package com.example.krazzybids.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.krazzybids.R;
import com.example.krazzybids.databinding.ActivityDashBoardBinding;
import com.example.krazzybids.fragment.HomeFragment;
import com.example.krazzybids.fragment.ProductFragment;
import com.example.krazzybids.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class DashBoard extends AppCompatActivity {

    ActivityDashBoardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dash_board);

        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        ft.replace(R.id.contentFrame, homeFragment, "HomeFragment");
        //text_name.setText("");
        ft.addToBackStack(null);
        ft.commit();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                switch (id) {

                    case R.id.home_fragment:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.product_fragment:
                        selectedFragment = new ProductFragment();
                        break;
                    case R.id.profile_fragment:
                        selectedFragment = new ProfileFragment();
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFrame, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }

        });

    }
}