package com.floodalert.app.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiVolley {

    public static final Object TAG = "VOLLEY_TAG";
    private static ApiVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private ApiVolley(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApiVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiVolley(context);
        }
        return mInstance;
    }

    public static synchronized ApiVolley getCachedInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);

        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);

        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

}
