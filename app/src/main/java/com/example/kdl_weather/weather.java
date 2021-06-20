package com.example.kdl_weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class weather extends AppCompatActivity {
    private static final String TAG = "activity_weather";

    private String cityId;//具体城市天气id
    private String cityName;
    private ArrayList<IndicesType> types;
    private String userName = "HE2106041138031460";
    private String key = "25bfe35227134df48ba5a1f98b3b3656";
    private String api_key = "e39f16e4fc5c44f3b450732f7feaef91";
    private String weatherCountry = "https://geoapi.qweather.com/v2/city/lookup?key=" + api_key;//和风天气城市信息查询api
    private String weather_3D = "https://devapi.qweather.com/v7/weather/3d?key=" + api_key;    //和风天气未来三天信息查询api


    private TextView mCityWeather, mWeather, mShidu, tv_pre_day1_maxtmp, tv_pre_day1_mintmp, tv_pre_day1_cond, tv_pre_day1_wind, tv_pre_day2_maxtmp, tv_pre_day2_mintmp,
            tv_pre_day2_cond, tv_pre_day2_wind, tv_pre_day3_maxtmp, tv_pre_day3_mintmp, tv_pre_day3_cond, tv_pre_day3_wind;

    String prvince;
    String city;//接收输入的城市
    private LinearLayout mWeatherInfo;
    private TextView mSuggestion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.d(TAG, "onCreate: activity_weather");
        Intent intent = this.getIntent();
        city = intent.getStringExtra("city");
        Log.d(TAG, "weathrer city: " + city);
        initView();
        HeConfig.init(userName, key);
        HeConfig.switchToDevService();
        queryWeather();

    }

    private void initView() {
        mCityWeather = (TextView) findViewById(R.id.city_weather);
        mWeather = (TextView) findViewById(R.id.weather);
        mShidu = (TextView) findViewById(R.id.shidu);
        mWeatherInfo = (LinearLayout) findViewById(R.id.weather_info);
        mSuggestion = (TextView) findViewById(R.id.suggestion);

        //未来三天天气
        tv_pre_day1_maxtmp = (TextView) findViewById(R.id.tv_pre_day1_maxtmp);
        tv_pre_day1_mintmp = (TextView) findViewById(R.id.tv_pre_day1_mintmp);
        tv_pre_day1_cond = (TextView) findViewById(R.id.tv_pre_day1_cond);
        tv_pre_day1_wind = (TextView) findViewById(R.id.tv_pre_day1_wind);

        tv_pre_day2_maxtmp = (TextView) findViewById(R.id.tv_pre_day2_maxtmp);
        tv_pre_day2_mintmp = (TextView) findViewById(R.id.tv_pre_day2_mintmp);
        tv_pre_day2_cond = (TextView) findViewById(R.id.tv_pre_day2_cond);
        tv_pre_day2_wind = (TextView) findViewById(R.id.tv_pre_day2_wind);

        tv_pre_day3_maxtmp = (TextView) findViewById(R.id.tv_pre_day3_maxtmp);
        tv_pre_day3_mintmp = (TextView) findViewById(R.id.tv_pre_day3_mintmp);
        tv_pre_day3_cond = (TextView) findViewById(R.id.tv_pre_day3_cond);
        tv_pre_day3_wind = (TextView) findViewById(R.id.tv_pre_day3_wind);

    }

    private void queryWeather() {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(weatherCountry + "&" + "location=" + city);
                    Log.d(TAG, "cityurl:" + url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();//开启一个url的连接，用HttpURLConnection连接方式处理
                    connection.setRequestMethod("GET");//设置连接对象的请求数据的方式
                    connection.setConnectTimeout(3000);//设置连接对象的请求超时的时间

                    //将请求返回的数据流转换成字节输入流对象
                    InputStream is = connection.getInputStream();
                    //将字节输入流对象转换成字符输入流对象
                    InputStreamReader isr = new InputStreamReader(is);
                    //创建字符输入缓冲流对象
                    BufferedReader br = new BufferedReader(isr);

                    StringBuffer sb = new StringBuffer();
                    String string;
                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb.append(string);
                    }

                    String result = sb.toString();

                    Log.d("weatherCountry", "" + result);


                    JSONObject cityIdInfo = new JSONObject(result);
                    JSONArray cityLocationInfo = cityIdInfo.getJSONArray("location");

                    Log.d(TAG, "run: " + cityLocationInfo);

                    JSONObject cityLocation_one = cityLocationInfo.getJSONObject(0);
                    cityId = cityLocation_one.getString("id");
                    cityName = cityLocation_one.getString("name");
                    prvince = cityLocation_one.getString("adm1");

                    //拼接字符串
                    String weatherApi = "https://devapi.qweather.com/v7/weather/now?key=" + api_key + "&" + "location=" + cityId;
                    Log.d("WeatherApi", "" + weatherApi);
                    getWeatherNow();
                    getIndices1D();
                    Log.d(TAG, "run: 1D");
                    getWeather3D();
                    Log.d(TAG, "run: 3D");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("error", "" + e);
                }

            }
        }.start();

    }

    public void getWeatherNow() {
        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文，海外城市默认为英文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        QWeather.getWeatherNow(weather.this, cityId, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Weather Now onError: ", e);

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(weatherNowBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherNowBean.getCode()) {
                    //此时返回数据
//                    getObsTime	实况观测时间	2013-12-30T13:14+08:00
//                    getFeelsLike	体感温度，默认单位：摄氏度	23
//                    getTemp	温度，默认单位：摄氏度	21
//                    getIcon	实况天气状况代码	100
//                    getText	实况天气状况代码	晴
//                    getWind360	风向360角度	305
//                    getWindDir	风向	西北
//                    getWindScale	风力	3-4
//                    getWindSpeed	风速，公里/小时	15
//                    getHumidity	相对湿度	40
//                    getPrecip	降水量	0
//                    getPressure	大气压强	1020
//                    getVis	能见度，默认单位：公里	10
//                    getCloud	云量	23
//                    getDew	实况云量	23

                    Basic basic = weatherNowBean.getBasic();
                    String location = basic.toString();
                    Log.d(TAG, "onSuccess: " + location);
                    mCityWeather.setText(prvince + cityName + "的天气");
                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();

                    String tmp = now.getTemp();
                    String cond_txt = now.getText();
                    String wind_dir = now.getWindDir();

                    mWeather.setText("当前温度：" + tmp + "℃，" + cond_txt + "，" + wind_dir);
                    String hum = now.getHumidity();
                    mShidu.setText(hum + "%");
                    Log.i(TAG, "onSuccess: " + tmp + cond_txt + wind_dir);
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherNowBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });


    }

    public void getIndices1D() {
        Log.i(TAG, "123: " + IndicesType.UV);
        types = new ArrayList<>();
        types.add(IndicesType.SPT);
        types.add(IndicesType.COMF);
        types.add(IndicesType.CW);
        types.add(IndicesType.UV);
        types.add(IndicesType.AC);


        QWeather.getIndices1D(weather.this, cityId, Lang.ZH_HANS, types, new QWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "Weather Indices onError: ", throwable);
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                Log.i(TAG, "onSuccess: success");
                List<IndicesBean.DailyBean> indicesBeanBases = indicesBean.getDailyList();
                String shushidu = indicesBeanBases.get(1).getLevel();//舒适度指数
                String shushidu2 = indicesBeanBases.get(1).getText();//舒适度建议
                String sport = indicesBeanBases.get(0).getLevel();//运动指数
                String sport2 = indicesBeanBases.get(0).getText();//运动建议
                String xiche = indicesBeanBases.get(2).getLevel();//洗车指数
                String xiche2 = indicesBeanBases.get(2).getText();//洗车建议
                Log.i(TAG, "onSuccess: success132");
                mSuggestion.setText("舒适度指数：" + shushidu + "\n" +
                        "舒适度建议：" + shushidu2 + "\n" +
                        "运动指数：" + sport + "\n" +
                        "运动建议：" + sport2 + "\n" +
                        "洗车指数：" + xiche + "\n" +
                        "洗车建议：" + xiche2 + "\n");
                Log.i(TAG, "onSuccess: " + shushidu + shushidu2 + sport + xiche);

            }
        });
    }

    public void getWeather3D() {
        try {
            URL url = new URL(weather_3D + "&" + "location=" + cityId);
            Log.d(TAG, "3Durl:" + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();//开启一个url的连接，用HttpURLConnection连接方式处理
            connection.setRequestMethod("GET");//设置连接对象的请求数据的方式
            connection.setConnectTimeout(3000);//设置连接对象的请求超时的时间

            //将请求返回的数据流转换成字节输入流对象
            InputStream is = connection.getInputStream();
            //将字节输入流对象转换成字符输入流对象
            InputStreamReader isr = new InputStreamReader(is);
            //创建字符输入缓冲流对象
            BufferedReader br = new BufferedReader(isr);

            StringBuffer sb = new StringBuffer();
            String string;
            //读文本
            while ((string = br.readLine()) != null) {
                sb.append(string);
            }

            String result = sb.toString();

            Log.d("weather3D", "" + result);


            JSONObject city_3D_Info = new JSONObject(result);
            JSONArray city3D_daily_Info = city_3D_Info.getJSONArray("daily");

            Log.d(TAG, "3Drun: " + city3D_daily_Info);

            JSONObject citydaily_one = city3D_daily_Info.getJSONObject(0);
            JSONObject citydaily_two = city3D_daily_Info.getJSONObject(1);
            JSONObject citydaily_three = city3D_daily_Info.getJSONObject(2);
            Log.d(TAG, "citydaily_one "+citydaily_one);
            Log.d(TAG, "citydaily_two "+citydaily_two);
            Log.d(TAG, "citydaily_three "+citydaily_three);
            String day1_maxtmp=citydaily_one.getString("tempMax");
            String day1_mintmp=citydaily_one.getString("tempMin");
            String day1_cond=citydaily_one.getString("textDay");
            String day1_wind=citydaily_one.getString("windDirDay");

            String day2_maxtmp=citydaily_two.getString("tempMax");
            String day2_mintmp=citydaily_two.getString("tempMin");
            String day2_cond=citydaily_two.getString("textDay");
            String day2_wind=citydaily_two.getString("windDirDay");

            String day3_maxtmp=citydaily_three.getString("tempMax");
            String day3_mintmp=citydaily_three.getString("tempMin");
            String day3_cond=citydaily_three.getString("textDay");
            String day3_wind=citydaily_three.getString("windDirDay");


            tv_pre_day1_maxtmp.setText("最高温度:" + day1_maxtmp);
            tv_pre_day1_mintmp.setText("最低温度:" + day1_mintmp);
            tv_pre_day1_cond.setText("天气:" + day1_cond);
            tv_pre_day1_wind.setText("风向:" + day1_wind);

            tv_pre_day2_maxtmp.setText("最高温度:" + day2_maxtmp);
            tv_pre_day2_mintmp.setText("最低温度:" + day2_mintmp);
            tv_pre_day2_cond.setText("天气:" + day2_cond);
            tv_pre_day2_wind.setText("风向:" + day2_wind);

            tv_pre_day3_maxtmp.setText("最高温度:" + day3_maxtmp);
            tv_pre_day3_mintmp.setText("最低温度:" + day3_mintmp);
            tv_pre_day3_cond.setText("天气:" + day3_cond);
            tv_pre_day3_wind.setText("风向:" + day3_wind);

            Message message = new Message();
            message.what = 1;
            weather.this.myHandler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("3Derror", "" + e);
        }
    }
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "handleMessage: " + msg);
                    mCityWeather.setVisibility(View.VISIBLE);
                    mWeatherInfo.setVisibility(View.VISIBLE);
                    tv_pre_day1_maxtmp.setVisibility(View.VISIBLE);
                    tv_pre_day2_maxtmp.setVisibility(View.VISIBLE);
                    tv_pre_day3_maxtmp.setVisibility(View.VISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };


}

