package com.mytoilet.whereisit;

import com.skt.Tmap.TMapMarkerItem;

public class MyMarker {
    private volatile static TMapMarkerItem instance;
    private MyMarker()
    {
    }
    public static TMapMarkerItem getInstance()
    {
        if(instance==null)
        {
            synchronized(TMapMarkerItem.class)
            {
                if(instance==null)
                {
                    instance = new TMapMarkerItem();
                }
            }
        }
        return instance;
    }
}
