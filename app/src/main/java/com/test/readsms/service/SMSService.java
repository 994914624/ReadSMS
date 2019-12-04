package com.test.readsms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.test.readsms.util.ToolBox;

/**
 * SMS Service
 */
public class SMSService extends Service {

    private static final String TAG = "SSS..SMSService";

    @Override
    public IBinder onBind(Intent arg0) {
        return new SMSBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "-- onStartCommand --");
        String status = ToolBox.isServiceRunning(getApplicationContext(), "com.test.readsms.service.SMSService");
        // 前台服务
        startForeground(1,ToolBox.createNotification(getApplicationContext(),status));
        // 监测短信
        ToolBox.watchSMS(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "-- onCreate --");
    }

    class SMSBinder extends Binder {

        public SMSService getService() {
            return SMSService.this;
        }
    }
}
