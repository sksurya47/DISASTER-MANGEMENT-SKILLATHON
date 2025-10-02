package com.floodalert.app.UI.intro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodalert.app.R;
import com.floodalert.app.UI.weather.PcpCheckWeatherActivity;
import com.floodalert.app.common.CustomLog;
import com.floodalert.app.common.PreferenceKey;
import com.floodalert.app.common.PreferencesSession;

public class PcpIntroActivity extends AppCompatActivity {

    private ImageView image;
    private TextView heading;
    private TextView description;
    private Button btn_previous;
    private Button btn_next;
    private Button btn_continue;
    private Context mContext;
    private int mInitialPosition = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.intro_activity);

        image = findViewById(R.id.image);
        heading = findViewById(R.id.heading);
        description = findViewById(R.id.description);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);
        btn_continue = findViewById(R.id.btn_continue);
        btn_previous.setVisibility(View.GONE);
        btn_continue.setVisibility(View.GONE);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mInitialPosition==3){
                    mInitialPosition = -1;
                }
                mInitialPosition++;
                updateView();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInitialPosition--;
                updateView();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateView(){

        CustomLog.trace("updateView");
        CustomLog.trace("mInitialPosition: "+mInitialPosition);

        if (mInitialPosition == 0) {
            image.setImageResource(R.drawable.intro_1);
            heading.setText(R.string.intro_one);
            description.setText(R.string.intro_one_description);
            btn_previous.setVisibility(View.GONE);
        }else if (mInitialPosition == 1) {
            image.setImageResource(R.drawable.intro_2);
            heading.setText(R.string.intro_two);
            description.setText(R.string.intro_two_description);
            btn_previous.setVisibility(View.VISIBLE);
        }else if (mInitialPosition == 2) {
            image.setImageResource(R.drawable.intro_3);
            heading.setText(R.string.intro_three);
            description.setText(R.string.intro_three_description);
            btn_previous.setVisibility(View.VISIBLE);
        }else if (mInitialPosition == 3) {
            image.setImageResource(R.drawable.intro_4);
            heading.setText(R.string.intro_four);
            description.setText(R.string.intro_four_description);
            btn_previous.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        }
    }
}
