package com.test.readsms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


import com.test.readsms.App;
import com.test.readsms.service.SMSService;
import com.test.readsms.service.SMSServiceConnection;
import com.test.readsms.util.ToolBox;

import static android.content.Intent.ACTION_VIEW;

/**
 * OnePixActivity
 */

public class OnePixActivity extends BaseActivity {

    private static OnePixActivity instance;
    private SMSServiceConnection connection = new SMSServiceConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("OnePix");
        Log.i("SSS.....OnePixActivity", "----- onCreate  -----");
        instance = this;
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        // 宽高
        params.height = 100;
        params.width = 100;
        window.setAttributes(params);

        //在 OnePix 页面也启动服务
        startSMSService();
        // 3秒后发通知
//        try {
//            App.getHandler().removeCallbacks(App.getRunnable());
//            App.getHandler().postDelayed(App.getRunnable(), 3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 锁屏时启动 OnePix
     */
    public static void startOnePixActivity(Context context) {
        Intent intent = new Intent(context, OnePixActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("OnePixActivity");
        context.startActivity(intent);
    }

    /*
     * 亮屏时 finish OnePix
     */
    public static void finishOnePixActivity() {
        if (instance != null) {
            instance.finish();
        }
    }

    /**
     * 启动 sms 服务
     */
    private void startSMSService() {
//        Intent intent = new Intent(getApplicationContext(), SMSService.class);
//        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("SSS.....OnePixActivity", "----- onStart  -----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("SSS.....OnePixActivity", "----- onResume  -----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("SSS.....OnePixActivity", "----- onPause  -----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SSS.....OnePixActivity", "----- onDestroy  -----");
        instance = null;
//        unbindService(connection);
    }
}
