package com.mytoilet.whereisit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.mytoilet.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    FileHandler converter;
    DatabaseReference snapShot;

    Runnable r= new Runnable(){
        @Override
        public void run()
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결
        FirebaseApp.initializeApp(this);
        snapShot = FirebaseDatabase.getInstance().getReference();
        if(snapShot==null)
        {
            DownloadTask task = new DownloadTask();
            task.execute();
        }
        else
        {
            handler2.postDelayed(r, 4000);
        }

    } // end of onCreate

    private class DownloadTask extends AsyncTask {

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
