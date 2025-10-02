package com.floodalert.app.UI.nearbyPlaces;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.R;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PcpPlacesAddActivity extends AppCompatActivity {

    private PreferencesSession preferencesSession;
    private View hld_action_bar;
    private View btn_back;
    private TextView action_bar_title;
    private EditText input_name;
    private EditText input_latitude;
    private EditText input_longitude;
    private Button btn_continue;
    public static final String __TITLE = "pcp_title";
    public static final String __TYPE = "pcp_type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        preferencesSession = new PreferencesSession(this);

        hld_action_bar = findViewById(R.id.hld_action_bar);
        btn_back = findViewById(R.id.btn_back);
        action_bar_title = findViewById(R.id.action_bar_title);
        input_name = findViewById(R.id.name);
        input_latitude = findViewById(R.id.latitude);
        input_longitude = findViewById(R.id.longitude);
        btn_continue = findViewById(R.id.btn_continue);

        if(getIntent()!=null){
            CustomLog.trace(getIntent().getStringExtra(__TITLE));
            action_bar_title.setText(getIntent().getStringExtra(__TITLE));
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONArray oldJsonArray = null;

                String name = input_name.getText().toString().trim();
                String latitude = input_latitude.getText().toString().trim();
                String longitude = input_longitude.getText().toString().trim();

                if(name.isEmpty()){
                    Toast.makeText(PcpPlacesAddActivity.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
                }else if(latitude.isEmpty()){
                    Toast.makeText(PcpPlacesAddActivity.this, "Please enter a place latitude", Toast.LENGTH_SHORT).show();
                }else if(longitude.isEmpty()){
                    Toast.makeText(PcpPlacesAddActivity.this, "Please enter a place longitude", Toast.LENGTH_SHORT).show();
                }else{

                    //get previous values
                    String mData = "";
                    if(getIntent()!=null){
                        if(getIntent().getStringExtra(PcpPlacesAddActivity.__TYPE).equals("1")){
                            mData = preferencesSession.getStringData(PreferenceKey.NEAR_BY_PLACES);
                        }else{
                            mData = preferencesSession.getStringData(PreferenceKey.NEAR_BY_BRIDGES);
                        }
                    }

                    if(!mData.isEmpty()){


                        try {
                            oldJsonArray = new JSONArray(mData);
                            CustomLog.trace("old jsonArray: "+oldJsonArray);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", name);
                        jsonObject.put("latitude", Double.parseDouble(latitude));
                        jsonObject.put("longitude", Double.parseDouble(longitude));

                        JSONArray finalJsonArray = null;

                        if(oldJsonArray!=null){
                            oldJsonArray.put(jsonObject);
                            finalJsonArray = oldJsonArray;
                        }else{
                            jsonArray.put(jsonObject);
                            finalJsonArray = jsonArray;
                        }

                        CustomLog.trace("final jsonArray: "+finalJsonArray.toString());


                        if(getIntent()!=null){
                            if(getIntent().getStringExtra(PcpPlacesAddActivity.__TYPE).equals("1")){
                                preferencesSession.saveStringData(PreferenceKey.NEAR_BY_PLACES,finalJsonArray.toString());
                            }else{
                                preferencesSession.saveStringData(PreferenceKey.NEAR_BY_BRIDGES,finalJsonArray.toString());
                            }

                            Toast.makeText(PcpPlacesAddActivity.this, "Place added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {

                    }

                }
            }
        });

    }
}


