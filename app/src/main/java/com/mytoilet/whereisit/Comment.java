package com.mytoilet.whereisit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Comment extends RealmObject {
    @PrimaryKey
    private int comment_id;

    private double rating;

    private RealmList<String> contents;



    public int getCommentId()
    {
        return comment_id;
    }
    public double getRating()
    {
        return rating;
    }
    public RealmList<String> getComments()
    {
        return contents;
    }



}
