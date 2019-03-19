package com.mytoilet.whereisit;


import android.content.Context;
import android.net.Uri;

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

    public FormatConverter() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.KOREA);
        Date date = new Date();
        String currentDay = format.format(date);
        if(currentDay.equals("25"))
            in = new BufferedInputStream(new URL("http://data.go.kr/widg/dsi/irosGnrlDataSetManage/downloadDataStoreToJson.do?id=uddi:fcnqnzns-yl87-yndq-imli-ujjgofjotxcf&fileName=%EC%A0%84%EA%B5%AD%ED%99%94%EC%9E%A5%EC%8B%A4%ED%91%9C%EC%A4%80%EB%8D%B0%EC%9D%B4%ED%84%B0").openStream());
    }

    void loadJSON(Context context){
        /*
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        StringBuilder sb = new StringBuilder();
        InputStream is = context.getAssets().open("toilet.json");
        JsonReader reader = new JsonReader(new InputStreamReader(is, "euc-kr"));
        JsonElement rootObject = parser.parse(reader).getAsJsonObject().get("fields");

        ToiletField[] example = gson.fromJson(rootObject, ToiletField[].class);
        List<ToiletField> list = Arrays.asList(example);
        for(ToiletField t:list)
        {
            System.out.println(t.getId());
        }
        */


    }





}
