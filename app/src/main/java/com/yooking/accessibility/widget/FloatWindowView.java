package com.yooking.accessibility.widget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.yooking.accessibility.BaseApplication;
import com.yooking.accessibility.R;
import com.yooking.accessibility.permission.rom.RomUtils;
import com.yooking.lib.utils.L;

/**
 * 覆盖其他页面的弹出层
 * Created by yooking on 2020/4/16.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class FloatWindowView {
    private View layoutView;

    private WindowManager manager;

    public ProgressBar progressBar;

    private Handler handler;

    private static class Holder {
        public static final FloatWindowView floatWindow = new FloatWindowView();
    }

    public static FloatWindowView getInstance() {
        return Holder.floatWindow;
    }

    private FloatWindowView() {
        Context context = BaseApplication.getInstance();
        handler = new Handler(Looper.getMainLooper());
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutView = LayoutInflater.from(context).inflate(R.layout.view_accessibility, null);
        progressBar = layoutView.findViewById(R.id.voice_diagnosis_progress);
    }

    private void setLayoutParamsType(WindowManager.LayoutParams layoutParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (RomUtils.isMiui()) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (RomUtils.isOppo()) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
    }

    public void showFloatWindow() {
        handler.post(() -> {
            try {
                progressBar.setProgress(0);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                setLayoutParamsType(layoutParams);
                layoutParams.width = -1;
                layoutParams.height = -1;
                layoutParams.format = 1;
                layoutParams.flags = 8;
                layoutParams.screenOrientation = 1;
                layoutParams.gravity = 17;
                manager.addView(layoutView, layoutParams);
                L.i("悬浮窗已显示，悬浮窗type：" + layoutParams.type);
                isShow = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stopFloatWindow() {
        handler.post(() -> {
            try {
                if (!(manager == null || layoutView == null) && isShow) {
                    manager.removeViewImmediate(layoutView);

                    isShow = false;
                }
                L.i("悬浮窗已关闭");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setFloatProgress(final int i) {
        handler.post(() -> {
            if (progressBar != null) {
                progressBar.setProgress(i);
            }
        });
    }

    private int progress = 0;

    public void progressAdd() {
        progress++;
        setFloatProgress(progress);
    }

    public int getProgress() {
        return progress;
    }

    private boolean isShow = false;

    private boolean isShow() {
        return isShow;
    }
}

