package com.mytoilet.whereisit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class Toilet implements Parcelable
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

    protected Toilet(Parcel in) {
        toilet_type = in.readString();
        toilet_name = in.readString();
        toilet_addr = in.createStringArrayList();
        isToiletBoth = in.readByte() != 0;
        in.readList(toilets_count, Integer.class.getClassLoader());
        contacts = in.readString();
        openTime = in.readString();
        lat = in.readFloat();
        lon = in.readFloat();
    }

    public static final Creator<Toilet> CREATOR = new Creator<Toilet>() {
        @Override
        public Toilet createFromParcel(Parcel in) {
            return new Toilet(in);
        }

        @Override
        public Toilet[] newArray(int size) {
            return new Toilet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toilet_type);
        dest.writeString(toilet_name);
        dest.writeStringList(toilet_addr);
        dest.writeByte((byte) (isToiletBoth ? 1 : 0));
        dest.writeList(toilets_count);
        dest.writeString(contacts);
        dest.writeString(openTime);
        dest.writeFloat(lat);
        dest.writeFloat(lon);
    }
}
