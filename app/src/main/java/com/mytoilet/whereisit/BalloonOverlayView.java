package com.mytoilet.whereisit;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mytoilet.R;

public class BalloonOverlayView implements Dialog {

    private Context mContext;

    public BalloonOverlayView(Context context) {
        this.mContext = context;

    }





    @Override
    public void openDialog() {
        final android.app.Dialog dlg = new android.app.Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.regional_dialog);
        dlg.show();
    }
}
