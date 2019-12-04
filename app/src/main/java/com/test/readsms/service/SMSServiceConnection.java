package com.test.readsms.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.test.readsms.util.ToolBox;

/**
 * Created by yzk on 2019-11-25
 */

public class SMSServiceConnection implements ServiceConnection {

    private static final String TAG = "SSS..ServiceConnection";
    private SMSService.SMSBinder smsBinder;


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "--- onServiceConnected ---：" + name);
        smsBinder = (SMSService.SMSBinder) service;
        ToolBox.watchSMS(smsBinder.getService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "--- onServiceDisconnected ---：" + name);
    }
}

