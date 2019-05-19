package com.mytoilet.whereisit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.mytoilet.R;


import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private FileHandler fileHandler;
    private Realm mRealm;

    Runnable r = () -> {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);

        setContentView(R.layout.activity_intro); // xml과 java소스를 연결

        FileTask task = new FileTask();
        task.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }




    private class FileTask extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog asyncDialog = new ProgressDialog(IntroActivity.this);

        @Override
        protected void onPreExecute()
        {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시 기다려 주십시오...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                RealmConfiguration config = new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
                mRealm = Realm.getInstance(config);
                RealmResults<Toilet> toiletList = mRealm.where(Toilet.class).findAll();

                if(toiletList.size()==0)
                {
                    fileHandler = new FileHandler(mRealm);
                    fileHandler.loadJSON(IntroActivity.this);
                }

                mRealm.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            handler.postDelayed(r,3000);
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }



    }

}
