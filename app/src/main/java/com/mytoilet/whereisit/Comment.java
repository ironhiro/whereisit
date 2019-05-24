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
    private float rating;
    private float sum;
    private RealmList<String> contents;



    public int getCommentId()
    {
        return comment_id;
    }
    public float getRating()
    {
        return rating;
    }
    public RealmList<String> getComments()
    {
        return contents;
    }
    public float getSum(){return sum;}

    public void setCommentId(int comment_id)
    {
        this.comment_id=comment_id;
    }
    public void setRating(float rating)
    {
        this.rating = rating;
    }
    public void setSum(float sum)
    {
        this.sum = sum;
    }
    public void addComment(String comment)
    {
        contents.add(comment);
    }
}
