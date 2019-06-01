package com.mytoilet.whereisit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.example.mytoilet.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

public class LocationNavigator extends Thread {
    private TMapView tMapView;
    private TMapMarkerItem item1, item2;


    public LocationNavigator(TMapView tMapView, TMapMarkerItem item1, TMapMarkerItem item2)
    {
        this.tMapView = tMapView;
        this.item1  =item1;
        this.item2 = item2;

    }

    public void run()
    {
        try{
            tMapView.removeAllTMapPolyLine();
            TMapPolyLine tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, item1.getTMapPoint(), item2.getTMapPoint());
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineAlpha(100);
            tMapPolyLine.setLineWidth(20);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
