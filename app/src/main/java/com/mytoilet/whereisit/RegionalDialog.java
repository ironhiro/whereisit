package com.mytoilet.whereisit;

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytoilet.R;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;


import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RegionalDialog implements com.mytoilet.whereisit.Dialog {
    private Context context;
    private TMapView tmapView;
    private Realm mRealm;
    private ListViewAdapter adapter;

    public RegionalDialog(Context context, TMapView tmapView, Realm mRealm,ListViewAdapter adapter) {
        this.context = context;
        this.tmapView = tmapView;
        this.mRealm = mRealm;
        this.adapter = adapter;
    }


    @Override
    public void openDialog() {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.regional_dialog);
        dlg.show();
        final Button okButton = (Button) dlg.findViewById(R.id.regional_confirm);
        final Button cancelButton = (Button) dlg.findViewById(R.id.regional_cancel);
        final EditText textView = (EditText) dlg.findViewById(R.id.autoCompleteTextView2);
        new Thread(() -> okButton.setOnClickListener(v -> {
            final TMapData tmapData = new TMapData();
            String strData = textView.getText().toString();
            tmapData.findAllPOI(strData, arrayList -> {

                new Thread()
                {
                    @Override
                    public void run()
                    {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        mRealm = Realm.getDefaultInstance();
                        int count = 0;
                        MainActivity activity = (MainActivity)context;
                        //우선 마커와 Circle 모두 제거
                        tmapView.removeAllMarkerItem();
                        tmapView.removeAllTMapCircle();
                        tmapView.removeAllTMapPolygon();
                        tmapView.removeAllTMapPOIItem();
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.custom_marker);
                        if(arrayList.size()==0)
                        {
                            activity.runOnUiThread(() -> {
                                Toast toast = Toast.makeText(activity, "키워드가 모호합니다. 찾으실 수 없습니다.", Toast.LENGTH_LONG);
                                toast.show();
                            });
                            mRealm.close();
                        }
                        else
                        {
                            tmapView.setCenterPoint(arrayList.get(0).getPOIPoint().getLongitude(),arrayList.get(0).getPOIPoint().getLatitude(),true);
                            RealmQuery<Toilet> query = mRealm.where(Toilet.class);
                            RealmResults<Toilet> toiletList = query.findAll();
                            toiletList = activity.filterMarker(toiletList);
                            int size = toiletList.size();
                            for (int i = 0; i < size; i++) {
                                TMapMarkerItem item = new TMapMarkerItem();
                                if (toiletList.get(i).lat == 0 || toiletList.get(i).lon == 0)
                                    continue;
                                else {
                                    item.setTMapPoint(new TMapPoint(toiletList.get(i).lat, toiletList.get(i).lon));

                                }

                                double distance = activity.getDistance(arrayList.get(0).getPOIPoint().getLatitude(),arrayList.get(0).getPOIPoint().getLongitude(), item.latitude, item.longitude);
                                if (distance < 2) {
                                    item.setIcon(bitmap);
                                    item.setVisible(TMapMarkerItem.VISIBLE);
                                    item.setName(toiletList.get(i).toilet_name);
                                    adapter.addItem(item.getName(), toiletList.get(i).toilet_addr1.equals("")?toiletList.get(i).toilet_addr2:toiletList.get(i).toilet_addr1,distance);
                                    tmapView.addMarkerItem(String.valueOf(toiletList.get(i).toilet_id), item);
                                    tmapView.setMapPosition(TMapView.POSITION_DEFAULT);

                                    count++;
                                }
                            }
                            TMapCircle circle = new TMapCircle();
                            circle.setCenterPoint(tmapView.getCenterPoint());
                            circle.setAreaColor(Color.GRAY);
                            circle.setAreaAlpha(100);
                            circle.setRadius(2000);
                            circle.setRadiusVisible(true);
                            tmapView.addTMapCircle("범위",circle);
                            adapter.sort();
                            adapter.notifyDataSetChanged();
                            int finalCount = count;
                            activity.runOnUiThread(() -> {
                                Toast toast = Toast.makeText(activity, "검색 결과 " + finalCount + "개 찾았습니다.", Toast.LENGTH_LONG);
                                toast.show();
                            });

                            mRealm.close();
                        }

                    }
                }.start();

            });
            dlg.dismiss();
        })).run();


        cancelButton.setOnClickListener(v -> dlg.dismiss());
    }


}
