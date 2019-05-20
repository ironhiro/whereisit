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
import com.skt.Tmap.TMapData;

public class BalloonDialog implements Dialog {

    private TMapData tmapData;
    private Context mContext;
    private Toilet toilet;

    public BalloonDialog(Context context,Toilet toilet) {
        this.mContext = context;
        this.toilet = toilet;
        this.tmapData = new TMapData();
    }





    @Override
    public void openDialog() {
        final android.app.Dialog dlg = new android.app.Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.bubble_popup);
        dlg.show();
        TextView title = (TextView)dlg.findViewById(R.id.bubble_title);
        title.setText(toilet.toilet_name);
        TextView subTitle = (TextView)dlg.findViewById(R.id.bubble_subtitle);
        subTitle.setText(toilet.toilet_addr1);


        Button navigation = (Button)dlg.findViewById(R.id.button_pathfind);
        navigation.setOnClickListener(v -> {
            
            dlg.dismiss();
        });

        Button info = (Button)dlg.findViewById(R.id.button_toiletinfo);
        info.setOnClickListener(v -> {
            dlg.dismiss();
        });

        Button market = (Button)dlg.findViewById(R.id.button_nearMarket);
        market.setOnClickListener(v -> {
            dlg.dismiss();
        });

        Button comment = (Button)dlg.findViewById(R.id.button_comment);
        comment.setOnClickListener(v -> {
            CommentDialog dialog = new CommentDialog(mContext);
            dialog.openDialog();

            dlg.dismiss();
        });
    }
}
