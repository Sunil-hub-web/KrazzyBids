package com.example.krazzybids.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.krazzybids.R;
import com.example.krazzybids.databinding.ActivitySplashScreenBinding;



public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {

    ActivitySplashScreenBinding binding;
    Handler handler;
    private UiModeManager uiModeManager;
    // Animation
    Animation animZoomOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

        handler = new Handler();

        // load the animation
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);

        // set animation listener
        animZoomOut.setAnimationListener(this);

        binding.imageview.startAnimation(animZoomOut);


         /* handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // This method will be executed once the timer is over

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                Animatoo.animateZoom(SplashScreen.this);
            }
        }, 3000);*/
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        if (animation == animZoomOut) {

            Intent i = new Intent(SplashScreen.this, LoginPage.class);
            startActivity(i);
            Animatoo.INSTANCE.animateSlideRight(SplashScreen.this);

        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}