package com.mytoilet.whereisit;


import android.content.Context;
import com.example.mytoilet.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FileHandler extends Exception
{
    BufferedInputStream in;
    FirebaseDatabase firebaseDatabase;

    public FileHandler() throws IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.KOREA);
        Date date = new Date();
        String currentDay = format.format(date);
        System.out.println(currentDay);
        if(currentDay.equals("25")) {
            in = new BufferedInputStream(new URL("http://data.go.kr/widg/dsi/irosGnrlDataSetManage/downloadDataStoreToJson.do?id=uddi:fcnqnzns-yl87-yndq-imli-ujjgofjotxcf&fileName=%EC%A0%84%EA%B5%AD%ED%99%94%EC%9E%A5%EC%8B%A4%ED%91%9C%EC%A4%80%EB%8D%B0%EC%9D%B4%ED%84%B0").openStream());

        }
    }

    void loadJSON(Context context) throws IOException {
        InputStream fin = context.getResources().openRawResource(R.raw.toilet);
        if(fin!=null)
        {
            Gson gson = new Gson();
            InputStreamReader stream = new InputStreamReader(fin, "euc-kr");
            BufferedReader buffer = new BufferedReader(stream);
            String read = buffer.readLine();
            read = read.replace("\"\"","\"") //먼저 쌍따옴표를 "으로 치환
                        .replace(":\",",":\"\","); //빈 문자열이 있는 곳을 치환

            JsonParser parser = new JsonParser();
            JsonReader jsonReader = new JsonReader(new StringReader(read));
            jsonReader.setLenient(true);

            JsonElement secondObject = parser.parse(jsonReader).getAsJsonObject().get("records");
            Object[] test = gson.fromJson(secondObject, Object[].class);
            for(Object t : test)
            {
                System.out.println(t.toString());
            }
        }

    }

    void putToDatabase()
    {

    }







}
