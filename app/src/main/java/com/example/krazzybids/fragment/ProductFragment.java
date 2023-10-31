package com.example.krazzybids.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.krazzybids.databinding.FragmentProductBinding;

import java.util.concurrent.TimeUnit;

public class ProductFragment extends Fragment {

    FragmentProductBinding binding;

    private static final String FORMAT = "%02d:%02d:%02d";
    int seconds , minutes;
    int minute;
    long min;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProductBinding.inflate(inflater,container,false);

       /* new CountDownTimer(172800000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                String timeData = String.format(FORMAT, TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                binding.timerData.setText(timeData);
                binding.timerData1.setText(timeData);

            }

            public void onFinish() {


            }

        }.start();*/

        reverseTimer(9000,binding.timerData);


        return binding.getRoot();
    }

    public void reverseTimer(int Seconds, final TextView tv) {

        new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                tv.setText("TIME : " + String.format("%02d", hours)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                tv.setText("Completed");
            }
        }.start();
    }
}
