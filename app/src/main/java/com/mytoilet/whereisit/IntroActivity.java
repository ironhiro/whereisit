package com.mytoilet.whereisit;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.mytoilet.R;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable r = new Runnable(){
        @Override
        public void run(){
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          startActivity(intent);
          overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
          finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결
    } // end of onCreate

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(r, 4000); // 4초 뒤에 Runnable 객체 수행

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(r); // 예약 취소
    }


}
