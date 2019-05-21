package com.mytoilet.whereisit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.mytoilet.R;


public class AddRequestDialog implements com.mytoilet.whereisit.Dialog{
    private Context context;


    public AddRequestDialog(Context context) {
        this.context = context;
    }

    public void openDialog() {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.addrequest_dialog);
        dlg.show();
        final TextInputLayout text = (TextInputLayout) dlg.findViewById(R.id.text_layout);
        Button okButton = (Button) dlg.findViewById(R.id.request_confirm);
        Button cancelButton = (Button) dlg.findViewById(R.id.request_cancel);
        Button currentLocation = (Button) dlg.findViewById(R.id.getCurrent);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                try {
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email_address)});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "화장실 추가 요청");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, text.getEditText().getText().toString());
                    emailIntent.setType("application/image");
                    emailIntent.setPackage("com.google.android.gm");
                    context.startActivity(emailIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }




}
