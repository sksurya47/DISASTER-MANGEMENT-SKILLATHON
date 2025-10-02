package com.floodalert.app.UI.alertNotification;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.floodalert.app.R;
import com.floodalert.app.UI.home.PcpHomeActivity;
import com.floodalert.app.UI.splash.SplashActivity;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;
import com.floodalert.app.fcm.SkConfig;
import com.floodalert.app.fcm.SkNotificationUtils;

import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class PcpCountdownService extends Service {
    private static final String CHANNEL_ID = "CountdownServiceChannel";
    private static final long COUNTDOWN_TIME_MS = 60000; // 1 minute
    private static final long INTERVAL_MS = 1000; // 1 second

    private CountDownTimer countDownTimer;
    private static final int NOTIFICATION_ID = 1;

    PreferencesSession preferencesSession;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context =  this;
        preferencesSession = new PreferencesSession(this);
        createNotificationChannel();
        startForeground(1, getNotification());
        startCountdown();
    }

    private boolean isAppLocked(String packageName) {
        return packageName.equals("com.facebook.katana") || packageName.equals("com.twitter.android");
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME_MS, INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Optionally, update UI or log progress here
                boolean isFacebookOpen = isFacebookAppOpen(context);
                CustomLog.trace("isFacebookOpen: "+isFacebookOpen);
                CustomLog.trace("CountdownService: Time left: " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                // Execute your process when the countdown finishes
                CustomLog.trace("CountdownService: Countdown finished!");
                performBackgroundTask();
            }
        }.start();
    }

    private void performBackgroundTask() {
        // Your background process here
        CustomLog.trace("CountdownService: Performing background task...");
        preferencesSession.saveBooleanData(PreferenceKey.IS_FLOOD_COMING,true);

        Intent pushNotification = new Intent(SkConfig.PUSH_NOTIFICATION);
        pushNotification.putExtra("update", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        Intent resultIntent = new Intent(getApplicationContext(), PcpHomeActivity.class);
        showNotificationMessage(getApplicationContext(), "Flood Alert", "Flood is possible please prepared", "2024-23-01 16:03:17", resultIntent);

        SkNotificationUtils notificationUtils = new SkNotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, PcpHomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your location weather report")
                .setContentText("checking...")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Countdown Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @SuppressLint("NewApi")
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        SkNotificationUtils notificationUtils = new SkNotificationUtils(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    public boolean isFacebookAppOpen(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -1); // Check usage stats for the last minute

            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    calendar.getTimeInMillis(),
                    time
            );

            if (usageStatsList != null) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
                for (UsageStats usageStats : usageStatsList) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }

                if (!sortedMap.isEmpty()) {
                    String lastApp = sortedMap.get(sortedMap.lastKey()).getPackageName();
                    return lastApp.equals("com.facebook.katana");
                }
            }
        }
        return false;
    }
}
