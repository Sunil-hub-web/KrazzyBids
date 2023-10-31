package com.example.krazzybids.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.krazzybids.R;
import com.example.krazzybids.SessionManager;
import com.example.krazzybids.adapter.SliderAdapterExample;
import com.example.krazzybids.databinding.FragmentHomeBinding;
import com.example.krazzybids.modelclass.BannerModelClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    SliderAdapterExample sliderAdapterExample;
    ArrayList<BannerModelClass> bannerModel = new ArrayList<>();
    private static final String FORMAT = "%02d:%02d:%02d";
    int seconds , minutes;
    int minute;
    long min;
    GoogleSignInClient mGoogleSignInClient;
    SessionManager sessionManager;

    TextView timer_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
       // minute=Integer.parseInt("Your time in string form like 10");
       // min= minute*60*1000;
      //  counter(min);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), options);
        sessionManager = new SessionManager(getActivity());


        sliderAdapterExample = new SliderAdapterExample(getActivity(), bannerModel);
        binding.imageSlider.setSliderAdapter(sliderAdapterExample);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(3);
        binding.imageSlider.setAutoCycle(true);
        binding.imageSlider.startAutoCycle();

        binding.relLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout_Condition();
            }
        });

        new CountDownTimer(86400000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                @SuppressLint("DefaultLocale") String timeData = String.format(FORMAT,TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                binding.timerData.setText(timeData);
                binding.timerData1.setText(timeData);

            }

            public void onFinish() {

                countDown();

            }

        }.start();



        return binding.getRoot();
    }

    public void logout_Condition() {

        //Show Your Another AlertDialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.condition_logout);
        dialog.setCancelable(false);
        Button btn_No = dialog.findViewById(R.id.no);
        TextView textView = dialog.findViewById(R.id.editText);
        Button btn_Yes = dialog.findViewById(R.id.yes);

        btn_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.logoutUser();
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();

                dialog.dismiss();



                //finish();
                //System.exit(1);

            }
        });
        btn_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.editbackground);

    }

    public void countDown(){

        new CountDownTimer(86400000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                @SuppressLint("DefaultLocale") String timeData = String.format(FORMAT,TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                binding.timerData.setText(timeData);
                binding.timerData1.setText(timeData);

            }

            public void onFinish() {


            }

        }.start();
    }

}
