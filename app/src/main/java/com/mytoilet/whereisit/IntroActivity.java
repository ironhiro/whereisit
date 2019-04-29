package com.mytoilet.whereisit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.mytoilet.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    FileHandler converter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference childRef;
    TMapData tMapData = new TMapData();
    final ArrayList<TMapPOIItem> toilets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst",true);
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        childRef = firebaseDatabase.getReference("화장실목록");
        if(first) //최초 실행시
        {
            DownloadTask download = new DownloadTask();
            download.execute();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        }
        else {
            LoadTask load = new LoadTask();
            load.execute();
        }
    }

    private class LoadTask extends AsyncTask{

        ProgressDialog loading = new ProgressDialog(IntroActivity.this);

        @Override
        protected void onPreExecute() {
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("잠시만 기다려주세요..");
            loading.show();
            //작업 준비 코드 작성
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            new Thread()
            {
                public void run()
                {

                    childRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren())
                            {
                                Toilet toilet = data.getValue(Toilet.class);
                                if(toilet.lat == 0 || toilet.lon == 0)
                                {
                                    try {
                                        toilets.add(tMapData.findAddressPOI(toilet.toilet_addr.get(0)).get(0));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParserConfigurationException e) {
                                        e.printStackTrace();
                                    } catch (SAXException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    try {
                                        String addrData = tMapData.convertGpsToAddress(toilet.lat, toilet.lon);
                                        toilets.add(tMapData.findAddressPOI(addrData).get(0));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParserConfigurationException e) {
                                        e.printStackTrace();
                                    } catch (SAXException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            };

            handler.post(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("화장실 목록", toilets);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                    finish();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //작업 끝 코드 작성
            loading.dismiss();

            super.onPostExecute(o);
        }
    }


    private class DownloadTask extends AsyncTask { //초기실행시 데이터를 추가하는 클래스

        ProgressDialog loading = new ProgressDialog(IntroActivity.this);

        @Override
        protected void onPreExecute() {
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("잠시만 기다려주세요..");
            loading.show();
            //작업 준비 코드 작성
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {
                try {
                    converter = new FileHandler();
                    converter.loadJSON(IntroActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("화장실 목록", toilets);
                        startActivity(intent);

                        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        finish();
                    }
                });

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //작업 끝 코드 작성
            loading.dismiss();

            super.onPostExecute(o);
        }


    }



}
