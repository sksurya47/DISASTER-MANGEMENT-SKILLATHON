package com.floodalert.app.fcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.floodalert.app.UI.splash.SplashActivity;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

public class SkFirebaseMessagingService extends FirebaseMessagingService {
    //Get UserId
    Activity context;
    String fcmtoken = "";
    String mTitle = "Service Request";
    String mMessage = "New service request";
    String mOrderId="";
    PreferencesSession preferencesSession;

    @Override
    public void onNewToken(@NonNull String mToken) {
        super.onNewToken(mToken);
        CustomLog.trace("onNewToken: "+mToken);
        fcmtoken = mToken;
        preferencesSession = new PreferencesSession(this);
        preferencesSession.saveStringData(PreferenceKey.FCM_TOKEN,fcmtoken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Get UserID from Session
        CustomLog.trace("FCM_TAG: onMessageReceived");
        CustomLog.trace("FCM_TAG: remoteMessage: "+remoteMessage);

        preferencesSession = new PreferencesSession(this);

        Intent pushNotification = new Intent(SkConfig.PUSH_NOTIFICATION);
        pushNotification.putExtra("update", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
        showNotificationMessage(getApplicationContext(), mTitle, mMessage, "2024-23-01 16:03:17", resultIntent);

        SkNotificationUtils notificationUtils = new SkNotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
    }

    @SuppressLint("NewApi")
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        SkNotificationUtils notificationUtils = new SkNotificationUtils(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}
