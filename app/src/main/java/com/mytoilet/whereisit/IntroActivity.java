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

import java.io.IOException;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    FileHandler converter;
    DataSnapshot snapShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결
        FirebaseApp.initializeApp(this);

        DownloadTask task = new DownloadTask();
        task.execute();
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
