package com.test.readsms;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.gyf.cactus.Cactus;
import com.gyf.cactus.callback.CactusCallback;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.test.readsms.activity.OnePixActivity;
import com.test.readsms.service.SMSService;
import com.test.readsms.util.ToolBox;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by yzk on 2019-11-23
 */

public class App extends Application {

    private static final String TAG = "SSS..App";

    private static Handler handler;
    private static Runnable runnable;

    public static Handler getHandler() {
        return handler;
    }

    public static Runnable getRunnable() {
        return runnable;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToolBox.saveToSP(this,"launchTime",ToolBox.getDate());
        initSensorsDataAPI();
        keepLive();
    }

    /**
     * 保活
     */
    private void keepLive() {
        Cactus.getInstance()
                .isDebug(true)
                .hideNotificationAfterO(false)// 不隐藏，否则会 Bad notification for startForeground
                .addCallback(new CactusCallback(){

                    @Override
                    public void onStop() {
                        ToolBox.saveToSP(getApplicationContext(),"onStop",ToolBox.getDate());
                        Log.i(TAG, "----- Cactus onStop  ----- " );
                    }

                    @Override
                    public void doWork(int i) {
                        Log.i(TAG, "----- Cactus doWork  ----- " );
                    }
                })
                .register(this);

        // 主进程中启动服务，监测 sms
        if(mainProcess()){
            Intent intent = new Intent(getApplicationContext(), SMSService.class);
            if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_SMS") == PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 8.0 以上通过 startForegroundService启 动service
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
            initHandler();
        }
    }

    /**
     * 发送通知显示服务状态
     */
    private void initHandler() {
        handler = new Handler(getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    String status = ToolBox.isServiceRunning(getApplicationContext(), "com.test.readsms.service.SMSService");
                    // 发个通知
                    ToolBox.sendNotification(getApplicationContext(), status);
                    handler.postDelayed(runnable, 3000);
                    Log.i(TAG, "-----  SMSService  -----: " + status);
                }
            }
        };

        // 3秒后发通知
        try {
            App.getHandler().removeCallbacks(App.getRunnable());
            App.getHandler().postDelayed(App.getRunnable(), 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 Sensors Analytics SDK
     */
    private void initSensorsDataAPI() {
//        SAConfigOptions configOptions = new SAConfigOptions("https://sdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=21f2e56df73988c7");
//        // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
//        configOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_START |
//                SensorsAnalyticsAutoTrackEventType.APP_END |
//                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN |
//                SensorsAnalyticsAutoTrackEventType.APP_CLICK);
//        // 打开 crash 信息采集
//        configOptions.enableTrackAppCrash();
//        // 测试使用，打印日志便于观察。
//        configOptions.enableLog(true);
//        //传入 SAConfigOptions 对象，初始化神策 SDK
//        SensorsDataAPI.startWithConfigOptions(this, configOptions);

        SensorsDataAPI.sharedInstance(this,"https://sdktest.datasink.sensorsdata.cn/sa?project=yangzhankun&token=21f2e56df73988c7", SensorsDataAPI.DebugMode.DEBUG_AND_TRACK);
        SensorsDataAPI.sharedInstance().enableAutoTrack();
        SensorsDataAPI.sharedInstance().enableLog(true);
        SensorsDataAPI.sharedInstance().trackAppCrash();
    }

    private boolean mainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = null;
        if (am != null) {
            processInfos = am.getRunningAppProcesses();
            String mainProcessName = getPackageName();
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    Log.i("SSS...","------主进程----> "+info.processName);
                    return true;
                }
            }
        }
        return false;
    }
}
