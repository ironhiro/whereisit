package com.mytoilet.whereisit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler<E> {

    DatabaseReference childRef;
    private ChildEventListener mChild;
    private List<E> mList = new ArrayList<>();
    public DatabaseHandler(DatabaseReference childRef)
    {
        this.childRef = childRef;
    }

    public void addData(int id, E data)
    {
        childRef.child(String.valueOf(id)).setValue(data);
        mList.add(data);
    }

    public void deleteData(int id)
    {
        childRef.child(String.valueOf(id)).removeValue();
    }

    public void updateData(int id, E data)
    {
        childRef.child(String.valueOf(id)).setValue(data);
    }


    public ArrayList<E> getmList()
    {
        return (ArrayList<E>)mList;
    }
}
