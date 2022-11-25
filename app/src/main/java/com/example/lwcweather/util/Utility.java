package com.example.lwcweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.lwcweather.db.CoolWeatherDB;
import com.example.lwcweather.model.City;
import com.example.lwcweather.model.Province;
import com.example.lwcweather.model.County;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
// 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }//处理省级响应
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
// 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }//处理市级响应
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
// 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }//处理县级响应
    public static void handleWeatherResponse(Context context,String response)
    {
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONObject cityinfo=jsonObject.getJSONObject("cityInfo");
            JSONObject weatherinfo=jsonObject.getJSONObject("data");
            JSONArray data=weatherinfo.getJSONArray("forecast");
            String cityName=cityinfo.getString("city");
            String weatherCode=cityinfo.getString("citykey");
            String publishTime=cityinfo.getString("updateTime");
            String notice=data.getJSONObject(0).getString("notice");
            String temp1=data.getJSONObject(0).getString("low").substring(3);
            String temp2=data.getJSONObject(0).getString("high").substring(3);
            String weatherDesp=data.getJSONObject(0).getString("type");
            String mtemp1=data.getJSONObject(1).getString("low").substring(3);
            String mtemp2=data.getJSONObject(1).getString("high").substring(3);
            String htemp1=data.getJSONObject(2).getString("low").substring(3);

            String htemp2=data.getJSONObject(2).getString("high").substring(3);
            String mtype=data.getJSONObject(1).getString("type");
            String htype=data.getJSONObject(2).getString("type");
            saveWeatherinfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime,mtemp1,mtemp2,htemp1,htemp2,mtype,htype,notice);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public static void saveWeatherinfo(Context context,String cityname,String weathercode,String temp1,String temp2,String weatherdesp,String publishtime,String mtmp1,String mtmp2,String htmp1,String htmp2,String mtype,String htype,String notice){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年mm月dd日");
        Calendar c=Calendar.getInstance();
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityname);
        editor.putString("weather_code",weathercode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherdesp);
        editor.putString("publish_time",publishtime);
        editor.putString("current_data",sdf.format(c.getTime()));
        editor.putString("mtemp1",mtmp1);
        editor.putString("mtemp2",mtmp2);
        editor.putString("htemp1",htmp1);
        editor.putString("htemp2",htmp2);
        editor.putString("mtype",mtype);
        editor.putString("htype",htype);
        editor.putString("notice",notice);
        editor.commit();
    }
}
