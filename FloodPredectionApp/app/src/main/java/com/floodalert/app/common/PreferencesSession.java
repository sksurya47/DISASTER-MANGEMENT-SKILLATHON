package com.floodalert.app.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSession extends PreferenceKey {

    Context mContext;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    private static PreferencesSession mPreferencesSession = null;
    private static final String PREFERENCE_KEY = "PREF&E$RE@N@CEKEYSSPOS";

    public static PreferencesSession getInstance(Context context) {
        if (mPreferencesSession == null) {
            mPreferencesSession = new PreferencesSession(context);
        }
        return mPreferencesSession;
    }

    @SuppressLint("CommitPrefEdits")
    public PreferencesSession(Context context) {
        super();
        this.mContext = context;
        mPreferences = this.mContext.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void saveStringData(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void saveBooleanData(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public String getStringData(String urlKey) {
        return mPreferences.getString(urlKey, "");
    }

    public boolean getBooleanData(String urlKey) {
        return mPreferences.getBoolean(urlKey, false);
    }

}
