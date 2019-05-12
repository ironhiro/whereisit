package com.mytoilet.whereisit;

import android.app.Dialog;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;

import com.example.mytoilet.R;
import com.skt.Tmap.TMapData;


import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class RegionalDialog implements com.mytoilet.whereisit.Dialog {
    private Context context;
    private TMapView tmapView;
    private ArrayList<TMapPOIItem> items = new ArrayList<>();

    public RegionalDialog(Context context, TMapView tmapView) {
        this.context = context;
        this.tmapView = tmapView;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TMapData tmapData = new TMapData();
                        String strData = textView.getText().toString();
                        tmapData.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                            @Override
                            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.custom_marker);

                                for(int i=0;i<arrayList.size();i++)
                                {
                                    TMapPOIItem item = arrayList.get(i);
                                    MarkerOverlay marker = new MarkerOverlay(context, item.getPOIID(), item.getPOIName());
                                    String strID = "TMapMarkerItem"+i;
                                    marker.setPosition(0.2f,0.2f);
                                    marker.setTMapPoint(item.getPOIPoint());
                                    marker.setIcon(bitmap);

                                    tmapView.addMarkerItem2(strID,marker);
                                    break;
                                }
                                tmapView.setCenterPoint(arrayList.get(0).getPOIPoint().getLongitude(),arrayList.get(0).getPOIPoint().getLatitude(),true);

                                /*
                                TMapPolyLine polyline = null; //보행자 경로를 얻어옴
                                try {
                                    polyline = tmapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tmapView.getLocationPoint(), arrayList.get(0).getPOIPoint());
                                    tmapView.addTMapPolyLine("보행자 경로",polyline);  //TMapPolyline추가
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                }
                                */

                            }
                        });

                        dlg.dismiss();
                    }
                });

            }
        }).run();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }


}
