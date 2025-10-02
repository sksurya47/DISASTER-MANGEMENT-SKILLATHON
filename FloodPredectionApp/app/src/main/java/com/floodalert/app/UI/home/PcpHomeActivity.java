package com.floodalert.app.UI.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.floodalert.app.R;
import com.floodalert.app.UI.precaution.PcpPrecautionActivity;
import com.floodalert.app.UI.settings.PcpSettingsActivity;
import com.floodalert.app.UI.splash.SplashActivity;
import com.floodalert.app.UI.weather.PcpCheckWeatherActivity;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class PcpHomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context mContext;
    private DrawerLayout drawer_layout;
    private GridLayout menu_grid_layout;
    private View hld_alert;
    private View btn_continue;
    private View btn_menu;
    private View btn_open_map;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PreferencesSession preferencesSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        preferencesSession = new PreferencesSession(this);
        setContentView(R.layout.home_screen);

        drawer_layout = findViewById(R.id.drawer_layout);
        menu_grid_layout = findViewById(R.id.menu_grid_layout);
        btn_menu = findViewById(R.id.btn_menu);
        hld_alert= findViewById(R.id.hld_alert);
        btn_continue= findViewById(R.id.btn_continue);
        btn_open_map= findViewById(R.id.btn_open_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        renderNavMenuView();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferencesSession.saveBooleanData(PreferenceKey.IS_FLOOD_COMING,false);
                preferencesSession.saveBooleanData(PreferenceKey.IS_TIMER_SET,false);
                Intent intent = new Intent(PcpHomeActivity.this, PcpPrecautionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void renderNavMenuView() {
        if (mContext == null) return;

        ArrayList<String>navMenuArray = new ArrayList<>();
        navMenuArray.add("Check Weather");
        navMenuArray.add("Settings");

        for (int i = 0; i < navMenuArray.size(); i++) {
            View mView = getNavMenuView(navMenuArray.get(i),menu_grid_layout);
            if (mView != null) {
                menu_grid_layout.addView(mView);
            }
        }

    }

    private View getNavMenuView(String title, GridLayout menu_grid_layout) {
        if (mContext == null) return null;
        if (menu_grid_layout == null) return null;

        View mLineItem = LayoutInflater.from(mContext)
                .inflate(R.layout.nav_menu_line_item, menu_grid_layout, false);

        View hld_content = mLineItem.findViewById(R.id.hld_content);
        TextView label = mLineItem.findViewById(R.id.label);

        label.setText(title);

        hld_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();

                if(title.equals("Check Weather")){
                    Intent intent = new Intent(PcpHomeActivity.this, PcpCheckWeatherActivity.class);
                    startActivity(intent);
                }

                if(title.equals("Settings")){
                    Intent intent = new Intent(PcpHomeActivity.this, PcpSettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        btn_open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + PcpLocationUtils.destinationLat + "," +  PcpLocationUtils.destinationLong + "&origin=" +  PcpLocationUtils.currentLat + "," +  PcpLocationUtils.currentLong);

                // Create an Intent to launch Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Verify that the device has Google Maps installed
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Handle the case where Google Maps is not installed
                    // For example, show a message to the user or redirect them to the Play Store
                }
            }
        });

        return mLineItem;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Add a marker in current location and move the camera
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            PcpLocationUtils.currentLat = location.getLatitude();
                            PcpLocationUtils.currentLong = location.getLongitude();
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));

                            CustomLog.trace("destinationLat: "+PcpLocationUtils.destinationLat);
                            CustomLog.trace("destinationLong: "+PcpLocationUtils.destinationLong);
                            if (PcpLocationUtils.destinationLat > 0 && PcpLocationUtils.destinationLong > 0){
                                LatLng destination = new LatLng(PcpLocationUtils.destinationLat, PcpLocationUtils.destinationLong);
                                mMap.addMarker(new MarkerOptions().position(destination).title(PcpLocationUtils.placeName));
                                String url = getUrl(currentLocation, destination);
                                new FetchDirectionsTask(mMap).execute(url);
                                btn_open_map.setVisibility(View.VISIBLE);
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    }
                });


    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String key = "AIzaSyCF6XNEnkkR-GFRCMFShRfiDCK8Kn5Xj_o";
        String parameters = str_origin + "&" + str_dest + "&key=" + key;
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(preferencesSession.getBooleanData(PreferenceKey.IS_FLOOD_COMING)){
            hld_alert.setVisibility(View.VISIBLE);
        }else{
            hld_alert.setVisibility(View.GONE);
        }
    }
}
