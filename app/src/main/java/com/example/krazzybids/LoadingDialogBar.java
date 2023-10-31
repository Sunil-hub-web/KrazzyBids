package com.example.krazzybids;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

public class LoadingDialogBar {

    Context context;
    Dialog dialog;

    public LoadingDialogBar(Context context){
        this.context = context;
    }

    public void ShowDialog(String title){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogshow);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleSetName = dialog.findViewById(R.id.progessName);

        titleSetName.setText(title);
        dialog.show();
    }

    public void HideDialog(){
        dialog.dismiss();
    }
}
