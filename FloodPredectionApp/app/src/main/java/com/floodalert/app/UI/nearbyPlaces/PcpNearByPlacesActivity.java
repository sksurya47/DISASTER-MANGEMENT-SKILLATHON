package com.floodalert.app.UI.nearbyPlaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.R;
import com.floodalert.app.UI.home.PcpHomeActivity;
import com.floodalert.app.UI.home.PcpLocationUtils;
import com.floodalert.app.UI.splash.SplashActivity;
import com.floodalert.app.common.CommonUtils;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PcpNearByPlacesActivity extends AppCompatActivity {

    private PreferencesSession preferencesSession;
    private View hld_action_bar;
    private View hld_no_data;
    private GridLayout grid_layout;
    private View btn_back;
    private View btn_add;
    private View data_view;
    private TextView action_bar_title;
    public static final String __TITLE = "pcp_title";
    public static final String __TYPE = "pcp_type";
    public static final String __ACTION = "pcp_action";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_place_screen);

        preferencesSession = new PreferencesSession(this);

        hld_action_bar = findViewById(R.id.hld_action_bar);
        btn_back = findViewById(R.id.btn_back);
        btn_add = findViewById(R.id.btn_add);
        action_bar_title = findViewById(R.id.action_bar_title);
        hld_no_data = findViewById(R.id.hld_no_data);
        grid_layout = findViewById(R.id.grid_layout);
        data_view = findViewById(R.id.data_view);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PcpNearByPlacesActivity.this, PcpPlacesAddActivity.class);
                if(getIntent()!=null){
                    if(getIntent().getStringExtra(PcpPlacesAddActivity.__TYPE).equals("1")){
                        intent.putExtra(PcpPlacesAddActivity.__TITLE,getResources().getString(R.string.add_nearby_parking));
                        intent.putExtra(PcpPlacesAddActivity.__TYPE,"1");
                    }else{
                        intent.putExtra(PcpPlacesAddActivity.__TITLE,getResources().getString(R.string.add_nearby_bridges));
                        intent.putExtra(PcpPlacesAddActivity.__TYPE,"2");
                    }
                }
                startActivity(intent);
            }
        });

        if(getIntent()!=null){
            action_bar_title.setText(getIntent().getStringExtra(__TITLE));
        }

    }

    private void renderNearByPlaces() {

        String mData = "";

        if(getIntent()!=null){
            if(getIntent().getStringExtra(PcpPlacesAddActivity.__TYPE).equals("1")){
                mData = preferencesSession.getStringData(PreferenceKey.NEAR_BY_PLACES);
            }else{
                mData = preferencesSession.getStringData(PreferenceKey.NEAR_BY_BRIDGES);
            }
        }

        CustomLog.trace("mData: "+mData);

        if(CommonUtils.isEmptyStr(mData)){
            hld_no_data.setVisibility(View.VISIBLE);
            data_view.setVisibility(View.GONE);
            return;
        }

        hld_no_data.setVisibility(View.GONE);
        data_view.setVisibility(View.VISIBLE);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(mData);
            CustomLog.trace("old jsonArray: "+jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            View mView = getPlacesView(jsonArray.optJSONObject(i),grid_layout);
            if (mView != null) {
                grid_layout.addView(mView);
            }
        }
    }

    private View getPlacesView(JSONObject mObj, GridLayout menu_grid_layout) {
        if (menu_grid_layout == null) return null;

        View mLineItem = LayoutInflater.from(this)
                .inflate(R.layout.nearby_place_line_item, menu_grid_layout, false);

        View hld_content = mLineItem.findViewById(R.id.hld_content);
        TextView label = mLineItem.findViewById(R.id.label);
        View btn_route = mLineItem.findViewById(R.id.btn_route);

        if(getIntent()!=null){
            if(getIntent().getStringExtra(PcpNearByPlacesActivity.__ACTION).equals("1")){
                btn_route.setVisibility(View.VISIBLE);
            }else{
                btn_route.setVisibility(View.GONE);
            }
        }

        label.setText(mObj.optString("name"));

        btn_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PcpLocationUtils.placeName = mObj.optString("name");
                PcpLocationUtils.destinationLat = mObj.optDouble("latitude");
                PcpLocationUtils.destinationLong = mObj.optDouble("longitude");
                Intent intent = new Intent(PcpNearByPlacesActivity.this, PcpHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return mLineItem;
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderNearByPlaces();
    }
}
