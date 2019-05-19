package com.mytoilet.whereisit;


import android.content.Context;

import com.example.mytoilet.R;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.xml.sax.SAXException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import io.realm.Realm;
import io.realm.RealmList;


public class FileHandler extends Exception
{
    private BufferedInputStream in;

    private Realm mRealm;

    private InputStream fin;
    public FileHandler(Realm realm) throws IOException
    {

        this.mRealm = realm;
        /*
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.KOREA);
        Date date = new Date();
        String currentDay = format.format(date);



        if(Integer.parseInt(currentDay) >= 25 && Integer.parseInt(currentDay) <= 31)
        {

            jsonUpdater();
        }
        */
    }

    void loadJSON(Context context) throws IOException, ParserConfigurationException, SAXException {

        fin = context.getResources().openRawResource(R.raw.toilet);
        if(fin!=null)
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
            mRealm.beginTransaction();
            for(int i=0;i<rows.size();i++)
            {
                String toilet_type=(String)rows.get(i).get("구분");
                String toilet_name=(String)rows.get(i).get("화장실명");
                toilet_name=toilet_name.replace(".","");
                RealmList<String> toilet_addrs=new RealmList<String>();
                toilet_addrs.add((String)rows.get(i).get("소재지도로명주소"));
                toilet_addrs.add((String)rows.get(i).get("소재지지번주소"));
                boolean isToiletBoth;
                String toiletBoth = (String)rows.get(i).get("남녀공용화장실여부");
                if(toiletBoth.toUpperCase().equals("Y"))
                    isToiletBoth=true;
                else
                    isToiletBoth=false;
                RealmList<Integer> toilets = new RealmList<>();
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-대변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-소변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-장애인용대변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-장애인용소변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-어린이용대변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("남성용-어린이용소변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("여성용-대변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("여성용-장애인용대변기수")));
                toilets.add((int)Float.parseFloat((String)rows.get(i).get("여성용-어린이용대변기수")));
                String contacts = (String)rows.get(i).get("전화번호");
                String openTime = (String)rows.get(i).get("개방시간");
                double lat;
                String latitude = (String)rows.get(i).get("위도");
                if(latitude == null || latitude.equals(""))
                    lat = 0;
                else
                    lat = Float.parseFloat((String)rows.get(i).get("위도"));
                String longitude = (String)rows.get(i).get("경도");
                double lon;
                if(longitude == null || longitude.equals(""))
                    lon = 0;
                else
                    lon = Float.parseFloat((String)rows.get(i).get("경도"));


                Toilet toilet = mRealm.createObject(Toilet.class,i);
                toilet.toilet_name = (toilet_name);
                toilet.toilet_type = (toilet_type);
                toilet.isToiletBoth = (isToiletBoth);
                toilet.toilet_addr1 = toilet_addrs.get(0);
                toilet.toilet_addr2 = toilet_addrs.get(1);
                toilet.toilets_count=(toilets);
                toilet.contacts =(contacts);
                toilet.openTime = (openTime);
                toilet.lat = (lat);
                toilet.lon = (lon);

            }
            mRealm.commitTransaction();
            return;
        }


    }


    void jsonUpdater() throws IOException
    {
        in = new BufferedInputStream(new URL("http://data.go.kr/widg/dsi/irosGnrlDataSetManage/downloadDataStoreToJson.do?id=uddi:fcnqnzns-yl87-yndq-imli-ujjgofjotxcf&fileName=%EC%A0%84%EA%B5%AD%ED%99%94%EC%9E%A5%EC%8B%A4%ED%91%9C%EC%A4%80%EB%8D%B0%EC%9D%B4%ED%84%B0").openStream());

    }

}
