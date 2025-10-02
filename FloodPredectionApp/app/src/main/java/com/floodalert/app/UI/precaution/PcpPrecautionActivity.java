package com.floodalert.app.UI.precaution;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.R;
import com.floodalert.app.UI.intro.PcpIntroActivity;
import com.floodalert.app.UI.nearbyPlaces.PcpNearByPlacesActivity;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

public class PcpPrecautionActivity extends AppCompatActivity {

    private View btn_back;
    private View btn_steps;
    private View btn_nearby_parking;
    private View btn_nearby_bridges;
    PreferencesSession preferencesSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.precaution_screen);
        preferencesSession = new PreferencesSession(this);
        preferencesSession.saveBooleanData(PreferenceKey.IS_FLOOD_COMING,false);

        btn_back = findViewById(R.id.btn_back);
        btn_steps = findViewById(R.id.btn_steps);
        btn_nearby_parking = findViewById(R.id.btn_nearby_parking);
        btn_nearby_bridges = findViewById(R.id.btn_nearby_bridges);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpPrecautionActivity.this, PcpIntroActivity.class);
                startActivity(intent);
            }
        });

        btn_nearby_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpPrecautionActivity.this, PcpNearByPlacesActivity.class);
                intent.putExtra(PcpNearByPlacesActivity.__TITLE,getResources().getString(R.string.nearby_parking));
                intent.putExtra(PcpNearByPlacesActivity.__TYPE,"1");
                intent.putExtra(PcpNearByPlacesActivity.__ACTION,"1");
                startActivity(intent);
            }
        });

        btn_nearby_bridges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpPrecautionActivity.this, PcpNearByPlacesActivity.class);
                intent.putExtra(PcpNearByPlacesActivity.__TITLE,getResources().getString(R.string.nearby_bridges));
                intent.putExtra(PcpNearByPlacesActivity.__TYPE,"2");
                intent.putExtra(PcpNearByPlacesActivity.__ACTION,"1");
                startActivity(intent);
            }
        });
    }
}
