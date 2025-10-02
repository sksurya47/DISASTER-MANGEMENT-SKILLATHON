package com.floodalert.app.UI.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.R;
import com.floodalert.app.UI.alertNotification.PcpCountdownService;
import com.floodalert.app.UI.alertNotification.ServiceUtils;
import com.floodalert.app.UI.intro.PcpIntroActivity;
import com.floodalert.app.UI.nearbyPlaces.PcpNearByPlacesActivity;
import com.floodalert.app.UI.precaution.PcpPrecautionActivity;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

public class PcpSettingsActivity extends AppCompatActivity {

    private View btn_nearby_parking;
    private View btn_nearby_bridges;
    private View btn_back;
    private View btn_okay;
    private View hld_timer;
    private EditText time;
    PreferencesSession preferencesSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        preferencesSession = new PreferencesSession(this);

        btn_back = findViewById(R.id.btn_back);
        btn_nearby_parking = findViewById(R.id.btn_nearby_parking);
        btn_nearby_bridges = findViewById(R.id.btn_nearby_bridges);
        btn_okay = findViewById(R.id.btn_okay);
        hld_timer = findViewById(R.id.hld_timer);
        time = findViewById(R.id.time);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_nearby_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpSettingsActivity.this, PcpNearByPlacesActivity.class);
                intent.putExtra(PcpNearByPlacesActivity.__TITLE,getResources().getString(R.string.nearby_parking));
                intent.putExtra(PcpNearByPlacesActivity.__TYPE,"1");
                intent.putExtra(PcpNearByPlacesActivity.__ACTION,"0");
                startActivity(intent);
            }
        });

        btn_nearby_bridges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpSettingsActivity.this, PcpNearByPlacesActivity.class);
                intent.putExtra(PcpNearByPlacesActivity.__TITLE,getResources().getString(R.string.nearby_bridges));
                intent.putExtra(PcpNearByPlacesActivity.__TYPE,"2");
                intent.putExtra(PcpNearByPlacesActivity.__ACTION,"0");
                startActivity(intent);
            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String timeStr = time.getText().toString().trim();

                if(timeStr.isEmpty()){
                    Toast.makeText(PcpSettingsActivity.this, "Please enter a minutes", Toast.LENGTH_SHORT).show();
                }else{
                    // Set the alarm
                    preferencesSession.saveBooleanData(PreferenceKey.IS_TIMER_SET,true);
                    Intent serviceIntent = new Intent(PcpSettingsActivity.this, PcpCountdownService.class);
                    boolean isRunning = ServiceUtils.isServiceRunning(PcpSettingsActivity.this, PcpCountdownService.class);
                    if (isRunning) {
                        stopService(serviceIntent);
                    }
                    startService(serviceIntent);
                    hld_timer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(preferencesSession.getBooleanData(PreferenceKey.IS_TIMER_SET)){
            hld_timer.setVisibility(View.GONE);
        }else{
            hld_timer.setVisibility(View.VISIBLE);
        }
    }
}
