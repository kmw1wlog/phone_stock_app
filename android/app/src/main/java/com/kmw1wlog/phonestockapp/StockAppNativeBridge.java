package com.kmw1wlog.phonestockapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

public class StockAppNativeBridge {
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1201;
    private static final String CHANNEL_ID = "stock_live_alerts";

    private final Activity activity;
    private final Context context;

    public StockAppNativeBridge(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @JavascriptInterface
    public boolean isNotificationBridgeReady() {
        return true;
    }

    @JavascriptInterface
    public void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
    }

    @JavascriptInterface
    public void scheduleLocalNotification(String payloadJson) {
        try {
            createChannelIfNeeded();

            JSONObject payload = new JSONObject(payloadJson);
            int id = payload.optInt("id", (int) (System.currentTimeMillis() % Integer.MAX_VALUE));
            String title = payload.optString("title", "급등주 for you 알람");
            String body = payload.optString("body", "조건 충족이 감지되었습니다.");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
                return;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

            NotificationManagerCompat.from(context).notify(id, builder.build());
        } catch (Exception ignored) {
        }
    }

    private void createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "실시간 조건 알림",
            NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("급등주 for you 조건 감지 알림");
        manager.createNotificationChannel(channel);
    }
}
