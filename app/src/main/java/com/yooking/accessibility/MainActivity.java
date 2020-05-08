package com.yooking.accessibility;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yooking.accessibility.assignment.AssignmentFactory;
import com.yooking.accessibility.intent.IntentUtils;
import com.yooking.accessibility.permission.FloatPermissionUtils;
import com.yooking.accessibility.widget.FloatWindowView;
import com.yooking.lib.utils.L;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private BroadcastReceiver mReceiver;

    private final static int LONG_SLEEP = 1500;
    private final static int SHORT_SLEEP = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        findViewById(R.id.btn_start_auto).setOnClickListener(v -> startAccessibilitySettings());
    }

//    private void test() {
//        startActivity(IntentUtils.xiaomiAutoStart());
//
//        Handler handler = new Handler(getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                List<AccessibilityNodeInfo> list = iteratorTree(MyAccessibilityService.getInstance().getRootInActiveWindow());
//                for (AccessibilityNodeInfo info : list) {
//                    L.i("------------------------------------------------------------");
//                    L.i("查询到组件" + info.getText());
//                    L.i("查询到组件" + info.getViewIdResourceName());
//                    L.i("查询到组件" + info.getClassName());
//                    L.i("------------------------------------------------------------");
//                }
//            }
//        },3000);
//
//    }
//
//    private List<AccessibilityNodeInfo> iteratorTree(AccessibilityNodeInfo parent) {
//        List<AccessibilityNodeInfo> childList = new ArrayList();
//        if (parent == null) {
//            return childList;
//        } else {
//            for (int i = 0; i < parent.getChildCount(); ++i) {
//                AccessibilityNodeInfo child = parent.getChild(i);
//                childList.add(child);
//                if (child.getChildCount() > 0) {
//                    childList.addAll(this.iteratorTree(child));
//                }
//            }
//
//            return childList;
//        }
//    }

    private void startAccessibilitySettings() {
        //判断辅助权限是否开启
        if (!MyAccessibilityService.isStart()) {
            //注册广播
            registerBroadcastReceiver();
            try {
                Intent intent = IntentUtils.accessibilitySettings();
                startActivity(intent);
                Log.i("MainActivity", "打开辅助权限页面");
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        } else {
            startAutoSettings();
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyAccessibilityService.ACTION_NAME);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(MyAccessibilityService.BROADCAST_KEY, false)) {
                    //接收到广播后才能确认service是有值的
                    startAutoSettings();
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private void startAutoSettings() {
        //开启辅助功能
        L.i("辅助功能启动");

        //先返回当前页面

        backThisActivity(() -> {
            //1.判断是否具有悬浮在其他窗口前的权限
            if (!FloatPermissionUtils.checkPermission(context)) {
                FloatPermissionUtils.applyPermission(context);
                //在其他应用上层显示
                //if (!MyAccessibilityService.getInstance().clickCheckBox("在其他应用上层显示", true)) {
                String[] checkBoxIdArr = {"android:id/checkbox", "android:id/switch_widget"};
                CountDownLatch latch = new CountDownLatch(checkBoxIdArr.length);

                for (String checkBoxId : checkBoxIdArr) {
                    new Thread(() -> {
                        L.i(checkBoxId + "开启子线程");
                        MyAccessibilityService.getInstance().clickCheckBox(checkBoxId, true);
                        latch.countDown();
                        L.i(checkBoxId + "结束子线程");
                    }).start();
                }
                try {
                    latch.await();
                    L.i("回归主线程");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backThisActivity(this::run);
            } else run();
        });
    }

    private void run() {
        if (FloatPermissionUtils.checkPermission(context)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                FloatWindowView.getInstance().showFloatWindow();
                //开启任务
                handler.postDelayed(
                        () -> AssignmentFactory.run(MainActivity.this, AssignmentFactory.create()),
                        LONG_SLEEP
                );
            }, SHORT_SLEEP);
        }
    }


    private void backThisActivity(Back2ThisListener listener) {
        final int TAG_BACK = 0;
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == TAG_BACK)
                    if (!isActivityForeground()) {
                        MyAccessibilityService.getInstance().goBack();
                        sendEmptyMessageDelayed(TAG_BACK, LONG_SLEEP);
                    } else {
                        listener.callback();
                    }
            }
        };
        handler.sendEmptyMessage(TAG_BACK);

    }

    private interface Back2ThisListener {
        void callback();
    }

    /**
     * 判断当前Activity是否在前台
     */
    private boolean isActivityForeground() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            return MainActivity.class.getName().equals(cpn.getClassName());
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
