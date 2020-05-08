package com.yooking.accessibility.permission.rom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.yooking.lib.utils.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Rom 工具
 * Created by yooking on 2020/4/16.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class RomUtils {

    /**
     * System.getProperties()不返回与 getprop 相同的属性。要获取 getprop 属性，请尝试使用 Runtime.exec() 执行 getprop 并读取其标准输出。
     *
     * @param propName
     * @return
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            L.e("Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    L.e("Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 是否OPPO系统
     *
     * @return
     */
    public static boolean isOppo() {
        return !TextUtils.isEmpty(getSystemProperty("ro.build.version.opporom"));
    }

    /**
     * 是否VIVO系统
     *
     * @return
     */
    public static boolean isVivo() {
        return !TextUtils.isEmpty(getSystemProperty("ro.vivo.os.version"));
    }

    /**
     * 是否华为系统
     *
     * @return
     */
    public static boolean isHuawei() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    /**
     * 是否小米系统
     *
     * @return
     */
    public static boolean isMiui() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    /**
     * 是否魅族系统
     *
     * @return
     */
    public static boolean isMeizu() {
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id");
        return !TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag.toLowerCase().contains("flyme");
    }

    /**
     * 是否 360 系统
     *
     * @return
     */
    public static boolean isQihoo() {
        return Build.MANUFACTURER.contains("QiKU");
    }


    /**
     * 判断 Intent 是否有效
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 获取 emui 版本号
     */
    public static Double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                version = version.substring(1);
                if (version.contains(".")) {
                    version = version.substring(0, version.indexOf("."));
                }
                return Integer.parseInt(version);
            } catch (Exception e) {
                L.e("getContext miui version code error, version : " + version);
            }
        }
        return -1;
    }
}
