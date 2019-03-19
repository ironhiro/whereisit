package com.mytoilet.whereisit;


import android.content.Context;
import android.net.Uri;

import com.example.mytoilet.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FormatConverter extends Exception
{
    BufferedInputStream in;

    public FormatConverter() throws IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.KOREA);
        Date date = new Date();
        String currentDay = format.format(date);
        if(currentDay.equals("25"))
            in = new BufferedInputStream(new URL("http://data.go.kr/widg/dsi/irosGnrlDataSetManage/downloadDataStoreToJson.do?id=uddi:fcnqnzns-yl87-yndq-imli-ujjgofjotxcf&fileName=%EC%A0%84%EA%B5%AD%ED%99%94%EC%9E%A5%EC%8B%A4%ED%91%9C%EC%A4%80%EB%8D%B0%EC%9D%B4%ED%84%B0").openStream());
    }

    void loadJSON(Context context) throws IOException {
        InputStream in = context.getResources().openRawResource(R.raw.toilet);
        if(in!=null)
        {
            Gson gson = new Gson();
            InputStreamReader stream = new InputStreamReader(in, "euc-kr");
            BufferedReader buffer = new BufferedReader(stream);
            String read = buffer.readLine();
            read = read.replace("\"\"","\"") //먼저 쌍따옴표를 "으로 치환
                        .replace(":\",",":\"\","); //빈 문자열이 있는 곳을 치환

            JsonParser parser = new JsonParser();
            JsonReader jsonReader = new JsonReader(new StringReader(read));
            jsonReader.setLenient(true);
            JsonElement rootObject = parser.parse(jsonReader).getAsJsonObject().get("fields");
            ToiletField[] example = gson.fromJson(rootObject, ToiletField[].class);
            for(ToiletField temp: example)
            {
                System.out.println(temp.getId());
            }

        }

    }







}
