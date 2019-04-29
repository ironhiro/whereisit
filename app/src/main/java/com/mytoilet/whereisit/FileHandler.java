package com.mytoilet.whereisit;


import android.content.Context;

import com.example.mytoilet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FileHandler extends Exception
{
    private BufferedInputStream in;
    private DatabaseReference ref;
    private DatabaseReference childRef1,childRef2;
    private DatabaseHandler<Toilet> toilethandler;
    private DatabaseHandler<Comment> commenthandler;
    private InputStream fin;
    public FileHandler() throws IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.KOREA);
        Date date = new Date();
        String currentDay = format.format(date);
        ref = FirebaseDatabase.getInstance().getReference();
        childRef1 = ref.child("화장실목록");
        childRef2 = ref.child("댓글");
        toilethandler = new DatabaseHandler<>(childRef1);
        commenthandler = new DatabaseHandler<>(childRef2);

        if(Integer.parseInt(currentDay) >= 25 && Integer.parseInt(currentDay) <= 31)
        {
            jsonUpdater();
        }
    }

    void loadJSON(Context context) throws IOException {

        fin = context.getResources().openRawResource(R.raw.toilet);
        if(fin==null)
        {
            Type rowListType = new TypeToken<List<Map<String,Object>>>(){}.getType();
            Gson gson = new Gson();
            InputStreamReader stream = new InputStreamReader(fin, "euc-kr");
            BufferedReader buffer = new BufferedReader(stream);
            String read = buffer.readLine();
            read = read.replace("\"\"","\"") //먼저 쌍따옴표를 "으로 치환
                        .replace(":\",",":\"\",") //빈 문자열이 있는 곳을 치환
                        .replace("\".\"","\"0\"");

            JsonParser parser = new JsonParser();
            JsonReader jsonReader = new JsonReader(new StringReader(read));
            jsonReader.setLenient(true);

            JsonElement secondObject = parser.parse(jsonReader).getAsJsonObject().get("records");

            List<Map<String,Object>> rows = gson.fromJson(secondObject,rowListType); // MapList로 json데이터 넣어줌

            for(int i=0;i<rows.size();i++)
            {
                String toilet_type=(String)rows.get(i).get("구분");
                String toilet_name=(String)rows.get(i).get("화장실명");
                toilet_name=toilet_name.replace(".","");
                String[] toilet_addrs=new String[2];
                toilet_addrs[0] = (String)rows.get(i).get("소재지도로명주소");
                toilet_addrs[1] = (String)rows.get(i).get("소재지지번주소");
                boolean isToiletBoth;
                String toiletBoth = (String)rows.get(i).get("남녀공용화장실여부");
                if(toiletBoth.toUpperCase().equals("Y"))
                    isToiletBoth=true;
                else
                    isToiletBoth=false;
                Integer[] toilets = new Integer[9];
                toilets[0] = (int)Float.parseFloat((String)rows.get(i).get("남성용-대변기수"));
                toilets[1] = (int)Float.parseFloat((String)rows.get(i).get("남성용-소변기수"));
                toilets[2] = (int)Float.parseFloat((String)rows.get(i).get("남성용-장애인용대변기수"));
                toilets[3] = (int)Float.parseFloat((String)rows.get(i).get("남성용-장애인용소변기수"));
                toilets[4] = (int)Float.parseFloat((String)rows.get(i).get("남성용-어린이용대변기수"));
                toilets[5] = (int)Float.parseFloat((String)rows.get(i).get("남성용-어린이용소변기수"));
                toilets[6] = (int)Float.parseFloat((String)rows.get(i).get("여성용-대변기수"));
                toilets[7] = (int)Float.parseFloat((String)rows.get(i).get("여성용-장애인용대변기수"));
                toilets[8] = (int)Float.parseFloat((String)rows.get(i).get("여성용-어린이용대변기수"));
                String contacts = (String)rows.get(i).get("전화번호");
                String openTime = (String)rows.get(i).get("개방시간");
                float lat;
                String latitude = (String)rows.get(i).get("위도");
                if(latitude == null || latitude.equals(""))
                    lat = 0;
                else
                    lat = Float.parseFloat((String)rows.get(i).get("위도"));
                String longitude = (String)rows.get(i).get("경도");
                float lon;
                if(longitude == null || longitude.equals(""))
                    lon = 0;
                else
                    lon = Float.parseFloat((String)rows.get(i).get("경도"));
                Toilet toilet = new Toilet(toilet_type, toilet_name, toilet_addrs, isToiletBoth
                , toilets, contacts, openTime, lat, lon);
                toilethandler.addData(i,toilet);
                Comment comment = new Comment(i,null,null);
                commenthandler.addData(i,comment);
            }
            return;
        }


    }



    void jsonUpdater() throws IOException
    {
        in = new BufferedInputStream(new URL("http://data.go.kr/widg/dsi/irosGnrlDataSetManage/downloadDataStoreToJson.do?id=uddi:fcnqnzns-yl87-yndq-imli-ujjgofjotxcf&fileName=%EC%A0%84%EA%B5%AD%ED%99%94%EC%9E%A5%EC%8B%A4%ED%91%9C%EC%A4%80%EB%8D%B0%EC%9D%B4%ED%84%B0").openStream());

    }

}
