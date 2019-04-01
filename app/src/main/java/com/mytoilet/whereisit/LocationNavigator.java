package com.mytoilet.whereisit;

import android.graphics.Color;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class LocationNavigator {
    public void findPath(TMapView tMapView, TMapMarkerItem item1, TMapMarkerItem item2)
    {
        try{
            TMapPolyLine tMapPolyLine = new TMapData().findPathData(item1.getTMapPoint(), item2.getTMapPoint());
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
