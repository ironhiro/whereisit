package com.mytoilet.whereisit;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mytoilet.R;

public class BalloonOverlayView extends FrameLayout {

    private LinearLayout layout;
    private TextView title;
    private TextView subTitle;
    private Button button1, button2, button3;

    public BalloonOverlayView(Context context, String labelName, String id) {

        super(context);

        setPadding(10, 0, 10, 0);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);


        setupView(context, layout, labelName, id);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;
        addView(layout, params);
    }


    protected void setupView(Context context, final ViewGroup parent, String labelName, String id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view = inflater.inflate(R.layout.bubble_popup, parent);

        title = (TextView) view.findViewById(R.id.bubble_title);
        subTitle = (TextView) view.findViewById(R.id.bubble_subtitle);
        button1 = (Button)view.findViewById(R.id.button_comment);
        button2 = (Button)view.findViewById(R.id.button_nearMarket);
        button3 = (Button)view.findViewById(R.id.button_pathfind);

        setTitle(labelName);
        setSubTitle(id);
    }

    public void setTitle(String str) {
        title.setText(str);
    }

    public void setSubTitle(String str) {
        subTitle.setText(str);
    }


}
