package com.mytoilet.whereisit;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mytoilet.R;

public class ToiletInfoDialog implements Dialog {

    private Context context;
    private Toilet toilet;
    ToiletInfoDialog(Context context, Toilet toilet)
    {
        this.context=context;
        this.toilet = toilet;
    }

    @Override
    public void openDialog() {
        final android.app.Dialog dlg = new android.app.Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.toiletinfo_dialog);
        dlg.show();
        TextView toilet_name = (TextView)dlg.findViewById(R.id.toiletName);
        toilet_name.setText(toilet.toilet_name);
        TextView toilet_type = (TextView)dlg.findViewById(R.id.toiletType);
        toilet_type.setText(toilet.toilet_type);
        TextView toilet_both = (TextView)dlg.findViewById(R.id.toiletBoth);
        toilet_both.setText(toilet.isToiletBoth ? "예":"아니오");
        TextView toilet_address = (TextView)dlg.findViewById(R.id.toilet_address);
        if(toilet.toilet_addr1.equals(""))
            toilet_address.setText(toilet.toilet_addr2);
        else
            toilet_address.setText(toilet.toilet_addr1);
        TextView toilet_numbers = (TextView)dlg.findViewById(R.id.toiletNum);
        toilet_numbers.setText("남성용-대변기 수 : " + toilet.toilets_count.get(0) + "\n"
                                + "남성용-소변기 수 : " + toilet.toilets_count.get(1) + "\n"
                                + "남성용-장애인 대변기 수 : " + toilet.toilets_count.get(2) + "\n"
                                + "남성용-장애인 소변기 수 : " + toilet.toilets_count.get(3) + "\n"
                                + "남성용-어린이용 대변기 수 : " + toilet.toilets_count.get(4) + "\n"
                                + "남성용-어린이용 소변기 수 : " + toilet.toilets_count.get(5) + "\n"
                                + "여성용-대변기 수 : " + toilet.toilets_count.get(6) + "\n"
                                + "여성용-장애인 대변기 수 : " + toilet.toilets_count.get(7) + "\n"
                                + "여성용-어린이 대변기 수 : " + toilet.toilets_count.get(8) + "\n");
        TextView toilet_contacts = (TextView)dlg.findViewById(R.id.toiletContacts);
        toilet_contacts.setText(toilet.contacts);
        TextView toilet_openTime = (TextView)dlg.findViewById(R.id.toiletTime);
        toilet_openTime.setText(toilet.openTime);
        Button closeButton = (Button)dlg.findViewById(R.id.marker_close);
        closeButton.setOnClickListener(v -> dlg.dismiss());
    }
}
