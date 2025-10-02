package com.floodalert.app.UI.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.UI.home.PcpLocationUtils;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;
import com.google.firebase.FirebaseApp;
import com.floodalert.app.UI.home.PcpHomeActivity;
import com.floodalert.app.UI.intro.PcpIntroActivity;
import com.floodalert.app.R;

public class SplashActivity extends AppCompatActivity {

    PreferencesSession preferencesSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesSession = new PreferencesSession(this);
        PcpLocationUtils.destinationLat = 0;
        PcpLocationUtils.destinationLong = 0;
        PcpLocationUtils.placeName = "";
        setContentView(R.layout.splash_screen);
        FirebaseApp.initializeApp(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferencesSession.saveBooleanData(PreferenceKey.IS_FLOOD_COMING,false);
                preferencesSession.saveBooleanData(PreferenceKey.IS_TIMER_SET,false);
                Intent intent = new Intent(SplashActivity.this, PcpHomeActivity.class);
                startActivity(intent);
                finish();

            }
        },2000);
    }
}
