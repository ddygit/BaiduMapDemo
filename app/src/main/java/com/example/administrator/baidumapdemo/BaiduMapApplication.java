package com.example.administrator.baidumapdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BaiduMapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    SDKInitializer.initialize(getApplicationContext());

    }

}
