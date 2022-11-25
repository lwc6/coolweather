package com.example.lwcweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lwcweather.R;
import com.example.lwcweather.service.AutoUpdateService;
import com.example.lwcweather.util.HttpCallbackListener;
import com.example.lwcweather.util.HttpUtil;
import com.example.lwcweather.util.Utility;

public class WeatherActivity extends Activity  implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private RelativeLayout relativeLayout;
    private TextView mtmp1,mtmp2,htmp1,htmp2,mtype,htype,jtmp1,jtmp2,jtype,notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
// 初始化各控件
        notice=(TextView)findViewById(R.id.notice);
        jtype=(TextView)findViewById(R.id.jtype);
        jtmp1=(TextView)findViewById(R.id.jtemp1);
        jtmp2=(TextView)findViewById(R.id.jtemp2);
        mtmp1=(TextView)findViewById(R.id.mtemp1);
        mtmp2=(TextView)findViewById(R.id.mtemp2);
        mtype=(TextView)findViewById(R.id.mtype);
        htmp1=(TextView)findViewById(R.id.htemp1);
        htmp2=(TextView)findViewById(R.id.htemp2);
        htype=(TextView)findViewById(R.id.htype);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        relativeLayout=(RelativeLayout) findViewById(R.id.back_k);
        relativeLayout.getBackground().setAlpha(100);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
// 有县级代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();

                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://t.weather.sojson.com/api/weather/city/" + weatherCode;
        queryFromServer(address, "weatherCode");
    }
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
// 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
// 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }//elseif
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        jtype.setText(prefs.getString("weather_desp",""));
        jtmp1.setText(prefs.getString("temp1",""));
        jtmp2.setText(prefs.getString("temp2",""));

        mtmp1.setText(prefs.getString("mtemp1",""));
        mtmp2.setText(prefs.getString("mtemp2",""));
        mtype.setText(prefs.getString("mtype",""));

        htmp1.setText(prefs.getString("htemp1",""));
        htmp2.setText(prefs.getString("htemp2",""));
        htype.setText(prefs.getString("htype",""));
        notice.setText(prefs.getString("notice",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}