package com.mytoilet.whereisit;

import java.util.Arrays;
import java.util.List;

public class Toilet
{
    public String toilet_type;
    public String toilet_name;
    public List<String> toilet_addr;
    public boolean isToiletBoth;
    public List<Integer> toilets_count;
    public String contacts;
    public String openTime;
    public float lat;
    public float lon;
    public Toilet()
    {

    }
    public Toilet(String toilet_type, String toilet_name, String[] toilet_addr, boolean isToiletBoth
    , Integer[] toilets_count, String contacts, String openTime, float lat, float lon)
    {
        this.toilet_type = toilet_type;
        this.toilet_name = toilet_name;
        this.toilet_addr = Arrays.asList(toilet_addr);
        this.isToiletBoth = isToiletBoth;
        this.toilets_count = Arrays.asList(toilets_count);
        this.contacts = contacts;
        this.openTime = openTime;
        this.lat = lat;
        this.lon = lon;
    }

}
