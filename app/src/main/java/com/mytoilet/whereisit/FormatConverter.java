package com.mytoilet.whereisit;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FormatConverter extends Exception
{
    JSONParser parser = new JSONParser();
    String str = "";
    public void convertJSON(String fileName)
    {

        try
        {
            JSONArray jsonArray = (JSONArray)parser.parse(str);

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }





}
