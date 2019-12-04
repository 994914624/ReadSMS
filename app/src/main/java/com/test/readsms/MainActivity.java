package com.test.readsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.test.readsms.activity.BaseActivity;
import com.test.readsms.receiver.ScreenOnOffReceiver;
import com.test.readsms.service.SMSService;
import com.test.readsms.service.SMSServiceConnection;



import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseActivity {

    private ScreenOnOffReceiver screenOnOffReceiver;
    private SMSServiceConnection connection = new SMSServiceConnection();
    public static  TextView latestSMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latestSMS = findViewById(R.id.latest_sms);
        this.setTitle("Main");
        // 申请权限
        requestPermission(this);
        // 注册开屏、锁屏广播
        if (screenOnOffReceiver == null) {
            screenOnOffReceiver = new ScreenOnOffReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenOnOffReceiver, filter);
        }

//        // 3秒后发通知
//        try {
//            App.getHandler().removeCallbacks(App.getRunnable());
//            App.getHandler().postDelayed(App.getRunnable(), 3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * SMS 权限
     */
    private void requestPermission(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_SMS") != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{"android.permission.READ_SMS"}, 1001);
            } else {
                startSMSService();
            }
        } else {
            startSMSService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                startSMSService();
            } else {
                Toast.makeText(this, "需要读取 sms 权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 启动 sms 服务
     */
    private void startSMSService() {
        Intent intent = new Intent(getApplicationContext(), SMSService.class);
        getApplicationContext().bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 亮屏时启动 MainActivity
     */
    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.MAIN");
        context.getApplicationContext().startActivity(intent);
        // 再启动桌面
        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.addCategory(Intent.CATEGORY_HOME);
        context.getApplicationContext().startActivity(intent2);
    }

    /*
     * 按 back 启动桌面
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
