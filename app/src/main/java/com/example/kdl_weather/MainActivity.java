package com.example.kdl_weather;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.qweather.sdk.view.HeConfig;





public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private String userName = "HE2106041138031460";
    private String key = "25bfe35227134df48ba5a1f98b3b3656";
    private String api_key="e39f16e4fc5c44f3b450732f7feaef91";
    private EditText mCityEdit;
    private Button mSearch;
    String city;//接收输入的城市

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        HeConfig.init(userName,key);
        HeConfig.switchToDevService();

    }

    private void initView() {
        mCityEdit = findViewById(R.id.city_edit);
        mSearch = findViewById(R.id.search);
        mSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            default:
                break;
            case R.id.search:
                Intent intent = new Intent(MainActivity.this,weather.class);

                city = mCityEdit.getText().toString().trim();
                intent.putExtra("city",city);
                Log.d(TAG, "onClick: "+city);
                startActivity(intent);
                break;
        }
    }


}