package com.mytoilet.whereisit;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FormatConverter extends Exception
{
    JSONParser parser = new JSONParser();

    public void convertJSON(String fileName)
    {

        try
        {
            Object obj = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject)obj;

        }
        catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
