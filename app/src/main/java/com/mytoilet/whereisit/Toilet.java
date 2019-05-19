package com.mytoilet.whereisit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Toilet extends RealmObject {
    @PrimaryKey
    public int toilet_id;
    public String toilet_type;
    public String toilet_name;
    public String toilet_addr1, toilet_addr2;
    public boolean isToiletBoth;
    public RealmList<Integer> toilets_count;
    public String contacts;
    public String openTime;
    public double lat;
    public double lon;
    public Toilet()
    {

    }





}
