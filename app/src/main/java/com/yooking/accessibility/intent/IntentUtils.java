package com.yooking.accessibility.intent;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.yooking.accessibility.BaseApplication;
import com.yooking.accessibility.BuildConfig;

/**
 * 页面跳转工具类
 * Created by yooking on 2020/5/6.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class IntentUtils {

    /**
     * 跳转到通知管理
     */
    public static Intent notificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        }
        return intent;
    }

    /**
     * 跳转到 辅助功能 授权页面
     */
    public static Intent accessibilitySettings() {
        return new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    }

    /**
     * 跳转到 设置页
     */
    public static Intent settings() {
        return new Intent(Settings.ACTION_SETTINGS);
    }

    /**
     * 高耗电应用管理
     */
    public static Intent hightPowerManger() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$HighPowerApplicationsActivity");
        intent.setComponent(comp);
        return intent;
    }

    //------------------------------------huawei------------------------------------//

    public static Intent huaweiProtect() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
        intent.setComponent(comp);
        return intent;
    }

    public static Intent huaweiStartupNormalApp() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        intent.setComponent(comp);
        return intent;
    }

    public static Intent huaweiNotification() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
        intent.setComponent(comp);
        return intent;
    }

    public static Intent huaweiPowerManager() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.power.ui.HwPowerManagerActivity");
        intent.setComponent(comp);
        return intent;
    }


    //------------------------------------小米------------------------------------//

    public static Intent xiaomiAutoStart() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.OP_AUTO_START");
        return intent;
    }

    public static Intent xiaomiHiddenApps() {
        //神隐模式
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
        intent.putExtra("package_name", BaseApplication.getInstance().getPackageName());
        intent.putExtra("package_label", BaseApplication.getInstance().getAppName());
        return intent;
    }

    //------------------------------------魅族------------------------------------//

    public static Intent meizuAppsec() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BaseApplication.getInstance().getPackageName());
        return intent;
    }

    //------------------------------------oppo------------------------------------//

    /**
     * 跳转到oppo的权限设置
     */
    public static Intent oppoPermissions() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity
        ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity");
        intent.setComponent(comp);
        return intent;
    }
}
