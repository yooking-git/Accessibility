package com.yooking.accessibility;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.yooking.lib.accessibility.BaseAccessibilityService;
import com.yooking.lib.utils.L;

/**
 * 辅助功能服务
 * Created by yooking on 2020/5/7.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class MyAccessibilityService extends BaseAccessibilityService {

    private volatile static MyAccessibilityService mService;

    public static MyAccessibilityService getInstance() {
        return mService;
    }

    public static final String ACTION_NAME = "MyAccessibilityService";
    public static final String BROADCAST_KEY = "key";

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        L.i("onServiceConnected");
        mService = this;
        //通知Activity开始进行队列操作
        Intent intent = new Intent();
        intent.setAction(ACTION_NAME);
        intent.putExtra(BROADCAST_KEY, true);
        sendBroadcast(intent);
    }

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        L.i("onAccessibilityEvent:");
        //拦截所有非系统操作
        //获取当前页
    }

    //辅助功能中断
    @Override
    public void onInterrupt() {
        L.i("onInterrupt");
        mService = null;
    }

    //销毁辅助功能
    @Override
    public void onDestroy() {
        L.i("onDestroy");
        super.onDestroy();
        mService = null;
    }

    public static boolean isStart() {
        return mService != null;
    }
}
