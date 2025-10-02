package com.floodalert.app.common;

import android.util.Log;

public class CustomLog {

    public static void trace(String msg){
        Log.e("CUSTOM_LOG: ",""+msg);
    }
}
