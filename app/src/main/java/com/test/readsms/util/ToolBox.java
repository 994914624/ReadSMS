package com.test.readsms.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import androidx.core.app.NotificationCompat;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.test.readsms.App;
import com.test.readsms.MainActivity;
import com.test.readsms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by yzk on 2019-11-25
 */

public class ToolBox {

    private static final String TAG = "SSS..ToolBox";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static ContentResolver resolver;
    private static Cursor cursor;
    private static CursorAdapter cursorAdapter;

    /**
     * 展示一个本地通知
     */
    public static void sendNotification(Context context, String string) {
        String channelID = "100";
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyManager == null) return;
        Intent intent = new Intent();

        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置通知标题
                .setContentTitle("read sms " + string)
                // 点击通知后自动清除
                .setAutoCancel(false)
                // 设置通知内容
                .setContentText(getDate())
                .setVibrate(new long[]{0})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(mainPendingIntent);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelID, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(new long[]{0});
            channel.setDescription(new Date().toString());
            notifyManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.vibrate = null;
        notification.sound = null;
        notifyManager.notify(1, notification);

    }

    /**
     * 查看服务是否开启
     */
    public static String isServiceRunning(Context context, String ServiceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = null;
        if (myManager != null) {
            runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().toString().equals(ServiceName)) {
                    return "running";
                }
            }
        }
        return "stop";
    }

    /**
     * 日期
     */
    public static String getDate() {
        try {
            return format.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "format date error";
    }

    /**
     * 监测 sms
     */
    public static void watchSMS(final Context context) {
        if (cursorAdapter == null || cursor == null || resolver == null) {
            Log.i(TAG, "-- watchSMS --");
            resolver = context.getContentResolver();
            cursor = resolver.query(Uri.parse("content://sms"), null, null, null,
                    null);
            cursorAdapter = new CursorAdapter(context.getApplicationContext(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
                @Override
                public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
                    return null;
                }

                @Override
                public void bindView(View arg0, Context arg1, Cursor arg2) {
                }

                @Override
                protected void onContentChanged() {
                    super.onContentChanged();
                    Log.i(TAG, "-- onContentChanged --");
                    SensorsDataAPI.sharedInstance().track("onContentChanged");
                    ToolBox.saveToSP(context, "onContentChanged", ToolBox.getDate());
                    cursor = resolver.query(Uri.parse("content://sms"), null, null, null,
                            null);
                    // 第一条记录
                    if (cursor != null) {
                        cursor.moveToFirst();
                        final String address = cursor.getString(cursor.getColumnIndex("address"));
                        final String body = cursor.getString(cursor.getColumnIndex("body"));
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                // 先发送邮件
                                SendEmailUtil.sendEmail(String.format("%s\n\n内容：%s:\n\n发送时间：%s\n\nAndroidId：%s", address, body, ToolBox.getDate(),SensorsDataAPI.sharedInstance().getAnonymousId()));
                                // 发的服务器
                                String content ="";
                                try {
                                    content = URLEncoder.encode(body,"utf-8");
                                    ToolBox.sendHttpRequest(String.format("http://alkaidop.sensorsdata.cn/api/message/message-post.php?message=%s&number=%s&sn=%s", content,address ,SensorsDataAPI.sharedInstance().getAnonymousId()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.i(TAG, String.format("--sendSMS--: %s  :  %s", content , address));
                            }
                        }).start();

                        try {
                            // UI 展示一下
                            if(MainActivity.latestSMS!=null){
                                MainActivity.latestSMS.setText(String.format("最近一次读取短信的时间为：%s", ToolBox.getDate()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    /**
     * sp 保存标记
     */
    public static void saveToSP(Context context, String key, String name) {
        SharedPreferences sp = context.getSharedPreferences("yang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, name);
        editor.apply();
    }

    /**
     * 上报
     */
    public static void sendHttpRequest(String requestUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
            connection.connect();
            if (connection.getResponseCode() == 200) {
                Log.i(TAG, "-- getResponseCode 200 --:"+requestUrl);
                SensorsDataAPI.sharedInstance().track("SendSMSOK");
            } else {
                Log.i(TAG, String.format("-- getResponse  --:%s ####: %d", connection.getResponseMessage(), connection.getResponseCode()));
                try {
                    SensorsDataAPI.sharedInstance().track("SendSMSError",new JSONObject().put("reason",connection.getResponseMessage()));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                SensorsDataAPI.sharedInstance().track("SendSMSError",new JSONObject().put("reason",e.getMessage()));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            Log.i(TAG, "-- sendHttpRequest error  --");
        }
    }

    public static Notification createNotification(Context context, String string) {
        String channelID = "100";
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyManager == null) return null;
        Intent intent = new Intent();

        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置通知标题
                .setContentTitle("read sms " + string)
                // 点击通知后自动清除
                .setAutoCancel(false)
                // 设置通知内容
                .setContentText(getDate())
                .setVibrate(new long[]{0})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(mainPendingIntent);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelID, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(new long[]{0});
            channel.setDescription(new Date().toString());
            notifyManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.vibrate = null;
        notification.sound = null;
        return notification ;

    }

}
