package com.mytoilet.whereisit;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.mytoilet.R;

public class RegionalDialog {
    private Context context;
    public RegionalDialog(Context context)
    {
        this.context = context;
    }
    public void openDialog()
    {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.regional_dialog);
        dlg.show();
    }

}