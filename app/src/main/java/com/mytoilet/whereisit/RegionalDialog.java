package com.mytoilet.whereisit;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.mytoilet.R;
import com.skt.Tmap.TMapMarkerItem;

public class RegionalDialog implements com.mytoilet.whereisit.Dialog {
    private Context context;
    public RegionalDialog(Context context)
    {
        this.context = context;
    }
    public void openDialog(TMapMarkerItem item)
    {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.regional_dialog);
        dlg.show();
    }

    @Override
    public void openDialog() {

    }
}
