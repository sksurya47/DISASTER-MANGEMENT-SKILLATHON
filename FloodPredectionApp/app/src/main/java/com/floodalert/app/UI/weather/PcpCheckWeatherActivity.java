package com.floodalert.app.UI.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.floodalert.app.R;
import com.floodalert.app.UI.home.PcpLocationUtils;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.service.ApiStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class PcpCheckWeatherActivity extends AppCompatActivity {

    private Context mContext;
    private final String APP_ID = "dab3af44de7d24ae7ff86549334e45bd";
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final int REQUEST_CODE = 101;
    private TextView NameofCity;
    private TextView weatherState;
    private TextView Temperature;
    private GifImageView mWeatherIcon;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.weather_activity);

        weatherState = findViewById(R.id.weatherCondition);
        Temperature = findViewById(R.id.temperature);
        mWeatherIcon = findViewById(R.id.weatherIcon);
        NameofCity = findViewById(R.id.cityName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PcpLocationUtils.currentLat > 0 && PcpLocationUtils.currentLong > 0){
            getWeatherForCurrentLocationOnMap();
        }else{
            getWeatherForCurrentLocation();
        }

    }

    private void getWeatherForCurrentLocationOnMap() {
        CustomLog.trace("getWeatherForCurrentLocationOnMap");
        String Latitude = String.valueOf(PcpLocationUtils.currentLat);
        String Longitude = String.valueOf(PcpLocationUtils.currentLong);

        CustomLog.trace("Latitude: "+Latitude+" Longitude: "+Longitude);

        HashMap<String, String> params = new HashMap<>();
        params.put("lat", Latitude);
        params.put("lon", Longitude);
        params.put("appid", APP_ID);
        String URL = WEATHER_URL +"?"+"lat="+Latitude+"&lon="+Longitude+"&appid="+APP_ID;
        triggerAPICall(URL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

    }

    private void getWeatherForCurrentLocation() {
        CustomLog.trace("getWeatherForCurrentLocation");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                CustomLog.trace("Latitude: "+Latitude+" Longitude: "+Longitude);

                HashMap<String, String> params = new HashMap<>();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                String URL = WEATHER_URL +"?"+"lat="+Latitude+"&lon="+Longitude+"&appid="+APP_ID;
                triggerAPICall(URL);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        long MIN_TIME = 5000;
        float MIN_DISTANCE = 1000;
        String location_Provider = LocationManager.GPS_PROVIDER;
        mLocationManager.requestLocationUpdates(location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    private void triggerAPICall(String URL) {
        CustomLog.trace("triggerAPICall: "+URL);
        new ApiStringRequest(mContext, URL) {
            @Override
            protected void onServerResponse(String response) {
                Toast.makeText(PcpCheckWeatherActivity.this, "Weather updated", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    WeatherData mWeatherData = WeatherData.fromJson(jsonObject);
                    updateUI(mWeatherData);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onServerErrorResponse(String error) {

            }
        }.setMethod("GET").fetch();
    }

    private void updateUI(WeatherData mWeatherData) {
        Temperature.setText(mWeatherData.getmTemperature());
        NameofCity.setText(mWeatherData.getMcity());
        weatherState.setText(mWeatherData.getmWeatherType());
        int resourceID = getResources().getIdentifier(mWeatherData.getMicon(), "drawable", getPackageName());
        mWeatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PcpCheckWeatherActivity.this, "Location successfully obtained ", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                //user denied the permission
            }
        }
    }
}
