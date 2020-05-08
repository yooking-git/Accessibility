/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.yooking.accessibility.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.yooking.accessibility.permission.rom.HuaweiUtils;
import com.yooking.accessibility.permission.rom.MeizuUtils;
import com.yooking.accessibility.permission.rom.MiuiUtils;
import com.yooking.accessibility.permission.rom.OppoUtils;
import com.yooking.accessibility.permission.rom.QikuUtils;
import com.yooking.accessibility.permission.rom.RomUtils;
import com.yooking.lib.utils.L;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author zhaozp
 * @since 2016-10-17
 */

public class FloatPermissionUtils {
    public static boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtils.isMiui()) {
                return miuiPermissionCheck(context);
            } else if (RomUtils.isMeizu()) {
                return meizuPermissionCheck(context);
            } else if (RomUtils.isHuawei()) {
                return huaweiPermissionCheck(context);
            } else if (RomUtils.isQihoo()) {
                return qikuPermissionCheck(context);
            } else if (RomUtils.isOppo()) {
                return oppoROMPermissionCheck(context);
            }
        }
        return commonROMPermissionCheck(context);
    }

    private static boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtils.checkFloatWindowPermission(context);
    }

    private static boolean miuiPermissionCheck(Context context) {
        return MiuiUtils.checkFloatWindowPermission(context);
    }

    private static boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }

    private static boolean qikuPermissionCheck(Context context) {
        return QikuUtils.checkFloatWindowPermission(context);
    }

    private static boolean oppoROMPermissionCheck(Context context) {
        return OppoUtils.checkFloatWindowPermission(context);
    }

    private static boolean commonROMPermissionCheck(Context context) {
        //魅族系统单独适配
        if (RomUtils.isMeizu()) {
            return meizuPermissionCheck(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    L.e(e);
                }
            }
            return result;
        }
    }

    public static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.isMiui()) {
                miuiROMPermissionApply(context);
            } else if (RomUtils.isMeizu()) {
                meizuROMPermissionApply(context);
            } else if (RomUtils.isHuawei()) {
                huaweiROMPermissionApply(context);
            } else if (RomUtils.isQihoo()) {
                ROM360PermissionApply(context);
            } else if (RomUtils.isOppo()) {
                oppoROMPermissionApply(context);
            }
        } else {
            commonROMPermissionApply(context);
        }
    }

    private static void ROM360PermissionApply(final Context context) {
        QikuUtils.applyPermission(context);
    }

    private static void huaweiROMPermissionApply(final Context context) {
        HuaweiUtils.applyPermission(context);
    }

    private static void meizuROMPermissionApply(final Context context) {
        MeizuUtils.applyPermission(context);
    }

    private static void miuiROMPermissionApply(final Context context) {

        MiuiUtils.applyMiuiPermission(context);

    }

    private static void oppoROMPermissionApply(final Context context) {

        OppoUtils.applyOppoPermission(context);

    }

    /**
     * 通用 rom 权限申请
     */
    private static void commonROMPermissionApply(final Context context) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.isMeizu()) {
            meizuROMPermissionApply(context);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    commonROMPermissionApplyInternal(context);
                } catch (Exception e) {
                    L.e(e);
                }
            } else {
                L.d("user manually refuse OVERLAY_PERMISSION");
                //需要做统计效果
            }
        }
    }


    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

}
