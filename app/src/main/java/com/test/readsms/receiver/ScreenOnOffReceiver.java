package com.test.readsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.test.readsms.MainActivity;
import com.test.readsms.activity.OnePixActivity;


public class ScreenOnOffReceiver extends BroadcastReceiver {

    private static final String TAG = "SSS..ScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.i(TAG, "----- 息屏 ----");
//            OnePixActivity.startOnePixActivity(context);
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.i(TAG, "----- 亮屏 -----");
//            OnePixActivity.finishOnePixActivity();
//            MainActivity.startMainActivity(context);
        }
    }
}

