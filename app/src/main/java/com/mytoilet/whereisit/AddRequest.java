package com.mytoilet.whereisit;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AddRequest extends RealmObject
{
    @PrimaryKey
    public int request_id;
    public String address;
    public String photo_URL;
}
