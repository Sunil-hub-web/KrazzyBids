package com.example.krazzybids.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krazzybids.databinding.PackagesdetailsBinding;
import com.example.krazzybids.modelclass.Package_Model;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewModel> {

    Context context;
    ArrayList<Package_Model> packageModels;
    PackagesdetailsBinding binding;
    String selectItem = "";

    public PackageAdapter(ArrayList<Package_Model> packageModels, FragmentActivity activity) {

        this.context = activity;
        this.packageModels = packageModels;
    }

    @NonNull
    @Override
    public PackageAdapter.ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = PackagesdetailsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewModel(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapter.ViewModel holder, int position) {

        Package_Model packages = packageModels.get(position);
        holder.packagesdetailsBinding.packageName.setText(packages.getName());
        holder.packagesdetailsBinding.packagePrice.setText("Rs "+packages.getTotal_price()+" /-");
        holder.packagesdetailsBinding.offerprice.setText(packages.getDiscount_price()+"%"+" Extra Bid");
        holder.packagesdetailsBinding.totalBid.setText(packages.getTotal_bids()+" Bid");
        holder.packagesdetailsBinding.totalprice1.setText(packages.getTotal_price()+" Bid");

        holder.packagesdetailsBinding.totalprice2.setText(packages.getTotal_bids()+" Bid");

        holder.packagesdetailsBinding.totalprice1.setPaintFlags(holder.packagesdetailsBinding.totalprice1.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.packagesdetailsBinding.btnBYPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectItem.equals("Done")){

                    Toast.makeText(context, "Complete The Process", Toast.LENGTH_SHORT).show();

                }else{

                    selectItem = "Done";

                    holder.packagesdetailsBinding.btnBYPackages.setVisibility(View.GONE);
                    holder.packagesdetailsBinding.cardProgressbar.setVisibility(View.VISIBLE);
                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return packageModels.size();
    }

    public class ViewModel extends RecyclerView.ViewHolder {
        PackagesdetailsBinding packagesdetailsBinding;
        public ViewModel(PackagesdetailsBinding itemView) {
            super(itemView.getRoot());
            this.packagesdetailsBinding = itemView;
        }
    }
}
