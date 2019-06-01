package com.mytoilet.whereisit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytoilet.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import io.realm.Realm;

public class BalloonDialog implements Dialog {

    private TMapData tmapData;
    private Context mContext;
    private Toilet toilet;
    private TMapView tmapView;
    private Realm realm;

    public BalloonDialog(Context context, Toilet toilet, TMapView tmapView, Realm realm) {
        this.mContext = context;
        this.toilet = toilet;
        this.tmapData = new TMapData();
        this.tmapView = tmapView;
        this.realm = realm;
    }


    @Override
    public void openDialog() {
        final android.app.Dialog dlg = new android.app.Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.bubble_popup);
        dlg.show();
        TextView title = (TextView) dlg.findViewById(R.id.bubble_title);
        title.setText(toilet.toilet_name);
        TextView subTitle = (TextView) dlg.findViewById(R.id.bubble_subtitle);
        subTitle.setText(toilet.toilet_addr1);


        Button navigation = (Button) dlg.findViewById(R.id.button_pathfind);
        navigation.setOnClickListener(v -> {

            TMapPoint point1 = tmapView.getCircleFromID("범위").getCenterPoint();
            TMapMarkerItem item1 = new TMapMarkerItem();
            item1.setTMapPoint(point1);
            TMapMarkerItem item2 = tmapView.getMarkerItemFromID(String.valueOf(toilet.toilet_id));
            LocationNavigator navigator = new LocationNavigator(tmapView, item1, item2);
            navigator.start();
            try {
                navigator.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Activity mainActivity = (Activity)mContext;
            Button button = (Button)mainActivity.findViewById(R.id.tmap_navigation);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v1 -> {
                TMapTapi api = new TMapTapi(mContext);
                api.invokeNavigate("목적지",(float)item2.longitude, (float)item2.latitude, 0, true);
                button.setVisibility(View.GONE);
            });
            realm.close();
            dlg.dismiss();

        });

        Button info = (Button) dlg.findViewById(R.id.button_toiletinfo);
        info.setOnClickListener(v -> {
            ToiletInfoDialog dialog = new ToiletInfoDialog(mContext, toilet);
            dialog.openDialog();
            realm.close();
            dlg.dismiss();
        });

        Button market = (Button) dlg.findViewById(R.id.button_nearMarket);
        market.setOnClickListener(v -> {

            TMapMarkerItem item = tmapView.getMarkerItemFromID(String.valueOf(toilet.toilet_id));
            new Thread(() -> {
                ArrayList POIItem = null;
                try {
                    POIItem = tmapData.findAroundNamePOI(item.getTMapPoint(), "편의점");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                if (POIItem.size() == 0) {
                    Activity activity = (Activity)mContext;
                    activity.runOnUiThread(() -> {
                        Toast text = Toast.makeText(activity,"주변 마트를 찾을 수 없습니다.",Toast.LENGTH_LONG);
                        text.show();
                    });
                }
                else
                {
                    tmapView.removeAllTMapCircle();
                    tmapView.removeAllMarkerItem();
                    tmapView.addMarkerItem(item.getID(), item);
                    tmapView.setCenterPoint(item.longitude, item.latitude,true);
                    tmapView.addTMapPOIItem(POIItem);
                    Activity activity = (Activity)mContext;
                    ArrayList finalPOIItem = POIItem;

                    activity.runOnUiThread(() -> {
                        Toast text = Toast.makeText(activity,"총 " + finalPOIItem.size() + "개 존재합니다.",Toast.LENGTH_LONG);
                        text.show();
                        Button button = activity.findViewById(R.id.switchListButton);
                        button.setEnabled(false);
                    });


                }

            }).start();


            realm.close();
            dlg.dismiss();
        });

        Button comment = (Button) dlg.findViewById(R.id.button_comment);
        comment.setOnClickListener(v -> {
            CommentDialog dialog = new CommentDialog(mContext,realm,toilet);
            dialog.openDialog();
            dlg.dismiss();
        });
    }
}
