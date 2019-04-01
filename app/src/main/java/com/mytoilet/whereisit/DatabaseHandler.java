package com.mytoilet.whereisit;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DatabaseHandler<E> {
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference childRef;
    public DatabaseHandler(String s)
    {
        childRef = ref.child(s);
    }

    public void addData(int id, E data)
    {
        childRef.child(String.valueOf(id)).setValue(data);
    }

    public void deleteData(int id)
    {
        childRef.child(String.valueOf(id)).removeValue();
    }

    public void updateData(int id, E data)
    {
        childRef.child(String.valueOf(id)).setValue(data);
    }

    public void showData(int id)
    {

        searchData(id,new LoadDataCallback<E>(){
            @Override
            public void onDataLoaded(E data) {
                super.onDataLoaded(data);

            }
        });
    }

    public void searchData(@NonNull final int id, @NonNull final LoadDataCallback<E> callBack)
    {
        childRef.child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(String.valueOf(id)))
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
