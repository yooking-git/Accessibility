package com.yooking.accessibility.assignment;

import com.yooking.accessibility.BaseApplication;
import com.yooking.accessibility.intent.IntentUtils;
import com.yooking.accessibility.permission.rom.RomUtils;
import com.yooking.lib.assignment.Factory;
import com.yooking.lib.assignment.StepHelper;
import com.yooking.lib.assignment.entity.AssignmentEntity;
import com.yooking.lib.utils.L;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 任务工厂
 * Created by yooking on 2020/5/7.
 * Copyright (c) 2020 yooking. All rights reserved.
 */
public class AssignmentFactory extends Factory {


    private static AssignmentFactory instance;
    public static synchronized AssignmentFactory getInstance() {
        if (instance == null) {
            instance = new AssignmentFactory();
        }
        return instance;
    }


    @Override
    public long getDuration() {
        return 1500;
    }

    @Override
    public Queue<AssignmentEntity> defCreate() {
        Queue<AssignmentEntity> queue = new LinkedList<>();
        if (RomUtils.isHuawei()) {
            queue.addAll(HuaweiFactory.create());
        } else if (RomUtils.isMiui()) {
            queue.addAll(XiaomiFactory.create());
        } else if (RomUtils.isOppo()) {
            queue.addAll(OppoFactory.create());
        } else if (RomUtils.isMeizu()) {
            queue.addAll(MeizuFactory.create());
        }
        return queue;
    }

    static class HuaweiFactory {

        private static int getVersion() {
            int version = -1;
            try {
                String systemProperty = RomUtils.getSystemProperty("ro.build.version.emui");
                if (systemProperty != null) {
                    String trim = systemProperty.replace("EmotionUI", "").replace("_", "").trim();
                    if (trim.contains(".")) {
                        trim = trim.substring(0, trim.indexOf("."));
                    }
                    version = Integer.valueOf(trim);
                }
            } catch (Exception ignored) {

            }
            return version;
        }

        static Queue<AssignmentEntity> create() {
            L.i("手机版本号：" + getVersion()); //目前适配10
            Queue<AssignmentEntity> queue = new LinkedList<>();
            queue.add(ignoreBatteryOptimization());
            queue.addAll(newMessageNotification());
            queue.add(selfStarting());
            return queue;
        }

        //忽略电池优化
        private static AssignmentEntity ignoreBatteryOptimization() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("忽略电池优化");

            assignment.addStep(StepHelper.intentStep(IntentUtils.hightPowerManger()))
                    .addStep(StepHelper.clickStep("不允许"))
                    .addStep(StepHelper.clickStep("所有应用"))
                    .addStep(StepHelper.clickStep(BaseApplication.getInstance().getAppName()))
                    .addStep(StepHelper.clickStep("不允许"))
                    .addStep(StepHelper.clickStep("确定"))
                    .addStep(StepHelper.backStep());

            return assignment;
        }

        //新消息通知
        private static Queue<AssignmentEntity> newMessageNotification() {
            AssignmentEntity ae1 = new AssignmentEntity();
            ae1.setName("新消息通知");
            AssignmentEntity ae2 = new AssignmentEntity();
            ae2.setName("通知锁屏显示");

            ae1.addStep(StepHelper.intentStep(IntentUtils.huaweiNotification()))
                    .addStep(StepHelper.clickStep(BaseApplication.getInstance().getAppName()))
                    .addStep(StepHelper.checkStep("允许通知", true))
                    .addStep(StepHelper.backStep());
            ae2.addStep(StepHelper.clickStep("锁屏通知"))
                    .addStep(StepHelper.clickStep("显示所有通知"))
                    .addStep(StepHelper.clickStep("更多通知设置"))
                    .addStep(StepHelper.checkStep("通知亮屏提示", true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep());

            Queue<AssignmentEntity> queue = new LinkedList<>();
            queue.add(ae1);
            queue.add(ae2);
            return queue;
        }

        //自启动
        private static AssignmentEntity selfStarting() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("自启动");

            assignment.addStep(StepHelper.intentStep(IntentUtils.huaweiStartupNormalApp()))
                    .addStep(StepHelper.checkStep(BaseApplication.getInstance().getAppName(), true))
                    .addStep(StepHelper.checkStep(BaseApplication.getInstance().getAppName(), false))
                    .addStep(StepHelper.checkStep("允许自启动", true))
                    .addStep(StepHelper.checkStep("允许关联启动", true))
                    .addStep(StepHelper.checkStep("允许后台活动", true))
                    .addStep(StepHelper.clickStep("确定"))
                    .addStep(StepHelper.backStep());

            return assignment;
        }
    }

    static class XiaomiFactory {
        private static int getVersion() {
            String version = RomUtils.getSystemProperty("ro.miui.ui.version.name");
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

        static Queue<AssignmentEntity> create() {
            L.i("手机版本号：" + getVersion()); //目前适配8 对应Android6
            Queue<AssignmentEntity> queue = new LinkedList<>();
            queue.add(selfStarting());
            queue.add(newMessageNotification());
            queue.add(ignoreBatteryOptimization());
            return queue;
        }

        private static AssignmentEntity selfStarting() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("自启动");

            //测试机型出现点击后无效的问题
            assignment.addStep(StepHelper.intentStep(IntentUtils.xiaomiAutoStart()))
                    .addStep(StepHelper.clickStep(BaseApplication.getInstance().getAppName()))
                    .addStep(StepHelper.checkStep("允许系统唤醒", true))
                    .addStep(StepHelper.checkStep("允许被其他应用唤醒", true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep());
            return assignment;
        }

        private static AssignmentEntity newMessageNotification() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("新消息通知");

            assignment.addStep(StepHelper.intentStep(IntentUtils.notificationSettings()))
                    .addStep(StepHelper.clickStep("自定义通知"))
                    .addStep(StepHelper.checkStep("允许通知", true))
                    .addStep(StepHelper.checkStep("优先", true))
                    .addStep(StepHelper.checkStep("悬浮通知", true))
                    .addStep(StepHelper.checkStep("锁屏通知", true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep());
            return assignment;
        }

        private static AssignmentEntity ignoreBatteryOptimization() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("神隐模式");

            assignment.addStep(StepHelper.intentStep(IntentUtils.xiaomiHiddenApps()))
                    .addStep(StepHelper.clickStep("无限制"))
                    .addStep(StepHelper.backStep());
            return assignment;
        }
    }

    static class MeizuFactory {
        private static int getVersion() {
            int version = -1;
            try {
                String systemProperty = RomUtils.getSystemProperty("ro.build.display.id");
                if (systemProperty != null) {
                    systemProperty = systemProperty.toLowerCase().replace("flyme", "");
                    systemProperty = systemProperty.trim();
                    if (systemProperty.contains(".")) {
                        systemProperty = systemProperty.substring(0, systemProperty.indexOf("."));
                    }
                    version = Integer.parseInt(systemProperty);
                }
            } catch (Exception ignore) {

            }
            return version;
        }

        static Queue<AssignmentEntity> create() {
            L.i("手机版本号：" + getVersion()); //目前适配6 对应Android5.1
            Queue<AssignmentEntity> queue = new LinkedList<>();
            queue.add(newMessageNotification());
            return queue;
        }

        private static AssignmentEntity newMessageNotification() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("新消息通知");
            assignment.addStep(StepHelper.intentStep(IntentUtils.meizuAppsec()))
                    .addStep(StepHelper.checkStep("通知栏消息", true))
                    .addStep(StepHelper.checkStep("悬浮窗", true))
                    .addStep(StepHelper.clickStep("后台管理"))
                    .addStep(StepHelper.clickStep("允许后台运行"))
                    .addStep(StepHelper.clickStep("锁屏下显示界面"))
                    .addStep(StepHelper.clickStep("允许"))
                    .addStep(StepHelper.backStep());

            return assignment;
        }
    }

    static class OppoFactory {
        private static int getVersion() {
            return -1;
        }

        static Queue<AssignmentEntity> create() {
            L.i("手机版本号：" + getVersion()); //目前适配10
            Queue<AssignmentEntity> queue = new LinkedList<>();
            queue.add(newMessageNotification());
            queue.add(batteryOptimization());
            queue.add(selfStarting());
            return queue;
        }


        private static AssignmentEntity newMessageNotification() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("新消息通知");

            assignment.addStep(StepHelper.intentStep(IntentUtils.notificationSettings()))
                    .addStep(StepHelper.checkStep("允许通知", true))
                    .addStep(StepHelper.backStep());
            return assignment;
        }

        private static AssignmentEntity batteryOptimization() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("应用速冻");

            assignment.addStep(StepHelper.intentStep(IntentUtils.settings()))
                    .addStep(StepHelper.clickStep("电池"))
                    .addStep(StepHelper.clickStep("自定义耗电保护"))
                    .addStep(StepHelper.clickStep(BaseApplication.getInstance().getAppName()))
                    .addStep(StepHelper.checkStep("允许后台运行", true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep())//返回电池页
                    .addStep(StepHelper.clickStep("应用速冻"))
                    .addStep(StepHelper.checkStep(BaseApplication.getInstance().getAppName(), false))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep());
            return assignment;
        }

        private static AssignmentEntity selfStarting() {
            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setName("自启动");
            assignment.addStep(StepHelper.intentStep(IntentUtils.oppoPermissions()))
                    .addStep(StepHelper.clickStep("自启动管理"))
                    .addStep(StepHelper.checkStep(BaseApplication.getInstance().getAppName(), true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.clickStep("关联启动管理"))
                    .addStep(StepHelper.checkStep(BaseApplication.getInstance().getAppName(), true))
                    .addStep(StepHelper.backStep())
                    .addStep(StepHelper.backStep());
            return assignment;
        }
    }
}
