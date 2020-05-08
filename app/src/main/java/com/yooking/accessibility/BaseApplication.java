package com.yooking.accessibility;

import android.app.Application;

/**
 * 基类
 * Created by yooking on 2020/5/7.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class BaseApplication extends Application {
    private volatile static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
