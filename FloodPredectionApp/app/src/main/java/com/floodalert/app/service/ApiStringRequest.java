package com.floodalert.app.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.floodalert.app.common.CustomLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiStringRequest {

    private final Context mContext;
    private final String mUrl;
    private int mMethod = Request.Method.GET;
    private Map<String, String> mParams;
    private Map<String, String> mHeaderParams;
    private String mRawData;
    private long mReqStartMs;
    private long mReqEndMs;

    protected ApiStringRequest(Context mContext, String mUrl)
    {
        this.mContext = mContext;
        this.mUrl = mUrl;
        CustomLog.trace(mUrl);
    }

    protected abstract void onServerResponse(String response);
    protected abstract void onServerErrorResponse(String error);

    public void fetch()
    {
        mReqStartMs = System.currentTimeMillis();
        StringRequest objRequest = new StringRequest
                (mMethod, mUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CustomLog.trace(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Integer success = jsonObject.optInt("success",1);
                            if(success==1){
                                onServerResponse(response);
                            }else{
                                onServerErrorResponse(jsonObject.optString("message","Error Response"));
                            }
                        } catch (Exception e) {
                            e.getMessage();
                            onServerErrorResponse(e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        checkVolleyErrorMessage(error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mParams!=null ? mParams : super.getParams();
            }
            @Override
            public  Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaderParams!=null ? mHeaderParams : super.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return mRawData.getBytes();
            }
        };

        objRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * Math.max(1,getTimeout()),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES * Math.max(1,getMaxRetries()),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through your singleton class.
        objRequest.setShouldCache(false);
        ApiVolley.getInstance(mContext).addToRequestQueue(objRequest);
    }

    private void checkVolleyErrorMessage(VolleyError error) {
        try {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                if (networkResponse.statusCode == 401) {
                    CustomLog.trace("Session Expired");
                    onServerErrorResponse("Session Expired");
                } else {
                    String jsonError = new String(networkResponse.data);
                    JSONObject jsonObject = new JSONObject(jsonError);
                    if (jsonObject.has("error")) {
                        CustomLog.trace(jsonObject.getJSONObject("error").getString("message"));
                        onServerErrorResponse(jsonObject.getJSONObject("error").getString("message"));
                    } else {
                        CustomLog.trace(jsonObject.getString("message"));
                        onServerErrorResponse(jsonObject.getString("message"));
                    }
                }
            }else{
                CustomLog.trace(String.valueOf(error));
                onServerErrorResponse(error.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            CustomLog.trace(String.valueOf(error));
            onServerErrorResponse(error.toString());
        }
    }

    protected int getMaxRetries() {
        return 1;
    }

    protected int getTimeout() {
        return 1;
    }

    public ApiStringRequest setMethod(String mMethod) {
        this.mMethod = mMethod.equals("POST") ? Request.Method.POST : Request.Method.GET;
        CustomLog.trace(mMethod);
        return this;
    }

    public ApiStringRequest setPostParams(Map<String, String> mParams) {
        this.mParams = mParams;
        CustomLog.trace(mParams.toString());
        return this;
    }

    public ApiStringRequest setRawData(String mRawData) {
        this.mRawData = mRawData;
        return this;
    }

    public ApiStringRequest setHeader(boolean canAddHeaderYN) {
        if(canAddHeaderYN){
            Map<String, String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "key=");
            this.mHeaderParams = headerParams;
            CustomLog.trace(""+mHeaderParams);
            return this;
        }
        return this;
    }
}
