package com.mytoilet.whereisit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Comment implements Parcelable {
    public int comment_id;
    public double rating;
    public List<String> contents;
    Comment() { }
    Comment(int comment_id, double rating, List<String> contents)
    {
        this.comment_id = comment_id;
        this.rating = rating;
        this.contents = contents;
    }

    protected Comment(Parcel in) {
        comment_id = in.readInt();
        rating = in.readDouble();
        contents = in.createStringArrayList();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(comment_id);
        dest.writeDouble(rating);
        dest.writeStringList(contents);
    }
}
